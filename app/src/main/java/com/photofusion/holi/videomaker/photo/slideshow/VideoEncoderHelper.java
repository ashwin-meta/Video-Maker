package com.photofusion.holi.videomaker.photo.slideshow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class to encode video from image sequence using Android's native MediaCodec and MediaMuxer
 */
public class VideoEncoderHelper {
    private static final String TAG = "VideoEncoderHelper";
    private static final String VIDEO_MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;
    private static final int I_FRAME_INTERVAL = 1;
    private static final int BIT_RATE = 6000000; // 6Mbps

    private MediaCodec videoEncoder;
    private MediaMuxer mediaMuxer;
    private int videoTrackIndex = -1;
    private int audioTrackIndex = -1;
    private boolean muxerStarted = false;
    private AtomicBoolean encodingComplete = new AtomicBoolean(false);

    private ProgressCallback progressCallback;

    public interface ProgressCallback {
        void onProgress(int progress);
        void onComplete(boolean success);
    }

    /**
     * Creates a video from a sequence of images with optional audio
     *
     * @param imageDir Directory containing images named img1.jpg, img2.jpg, etc.
     * @param outputPath Output video file path
     * @param audioPath Optional audio file path (can be null)
     * @param frameOverlay Optional frame overlay bitmap (can be null)
     * @param width Video width
     * @param height Video height
     * @param durationPerImageMs Duration for each image in milliseconds
     * @param totalDurationSec Total video duration in seconds
     * @param imageCount Number of images
     * @param callback Progress callback
     */
    public void createVideoFromImages(File imageDir, String outputPath, String audioPath,
                                      Bitmap frameOverlay, int width, int height,
                                      float durationPerImageMs, float totalDurationSec,
                                      int imageCount, ProgressCallback callback) {
        this.progressCallback = callback;

        new Thread(() -> {
            try {
                // Create output file
                File outputFile = new File(outputPath);
                if (outputFile.exists()) {
                    outputFile.delete();
                }

                // Initialize encoder and muxer
                setupVideoEncoder(width, height);
                mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

                // Calculate frame timing
                long frameDurationUs = (long) (durationPerImageMs * 1000); // Convert ms to microseconds
                int totalFrames = (int) (totalDurationSec * FRAME_RATE);
                int framesPerImage = (int) (FRAME_RATE * (durationPerImageMs / 1000.0f));

                Log.d(TAG, "Creating video: " + totalFrames + " frames, " + framesPerImage + " frames per image");

                // Encode video frames
                encodeFrames(imageDir, frameOverlay, width, height, imageCount, 
                           framesPerImage, totalFrames);

                // Add audio if provided
                if (audioPath != null && !audioPath.isEmpty()) {
                    addAudioTrack(audioPath, totalDurationSec);
                }

                // Finalize
                finishEncoding();

                if (progressCallback != null) {
                    progressCallback.onProgress(100);
                    progressCallback.onComplete(true);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error creating video", e);
                if (progressCallback != null) {
                    progressCallback.onComplete(false);
                }
            }
        }).start();
    }

    private void setupVideoEncoder(int width, int height) throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);

        videoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
        videoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    private void encodeFrames(File imageDir, Bitmap frameOverlay, int width, int height,
                             int imageCount, int framesPerImage, int totalFrames) throws IOException {
        videoEncoder.start();

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        long presentationTimeUs = 0;
        long frameDurationUs = 1000000L / FRAME_RATE;
        int currentFrame = 0;
        int currentImageIndex = 1;

        while (currentFrame < totalFrames && !encodingComplete.get()) {
            // Determine which image to use
            int imageIndex = Math.min(currentImageIndex, imageCount);
            File imageFile = new File(imageDir, "img" + imageIndex + ".jpg");

            if (!imageFile.exists()) {
                Log.w(TAG, "Image not found: " + imageFile.getAbsolutePath());
                break;
            }

            // Load and process image
            Bitmap imageBitmap = loadAndScaleBitmap(imageFile, width, height);
            if (imageBitmap == null) {
                Log.e(TAG, "Failed to load image: " + imageFile.getAbsolutePath());
                break;
            }

            // Apply frame overlay if provided
            if (frameOverlay != null) {
                imageBitmap = applyFrameOverlay(imageBitmap, frameOverlay, width, height);
            }

            // Encode this image for the appropriate number of frames
            int framesToEncode = Math.min(framesPerImage, totalFrames - currentFrame);
            for (int i = 0; i < framesToEncode && currentFrame < totalFrames; i++) {
                // Feed frame to encoder
                int inputBufferIndex = videoEncoder.dequeueInputBuffer(10000);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = videoEncoder.getInputBuffer(inputBufferIndex);
                    if (inputBuffer != null) {
                        inputBuffer.clear();
                        
                        // Convert bitmap to YUV420 format
                        byte[] yuvData = convertBitmapToYUV420(imageBitmap, width, height);
                        inputBuffer.put(yuvData);
                        
                        videoEncoder.queueInputBuffer(inputBufferIndex, 0, yuvData.length,
                                presentationTimeUs, 0);
                        
                        presentationTimeUs += frameDurationUs;
                        currentFrame++;

                        // Report progress
                        int progress = (currentFrame * 100) / totalFrames;
                        if (progressCallback != null && currentFrame % 10 == 0) {
                            progressCallback.onProgress(progress);
                        }
                    }
                }

                // Drain output
                drainEncoder(bufferInfo, false);
            }

            imageBitmap.recycle();

            // Move to next image
            if ((currentFrame / framesPerImage) >= currentImageIndex) {
                currentImageIndex++;
            }
        }

        // Send end of stream signal
        int inputBufferIndex = videoEncoder.dequeueInputBuffer(10000);
        if (inputBufferIndex >= 0) {
            videoEncoder.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs,
                    MediaCodec.BUFFER_FLAG_END_OF_STREAM);
        }

        // Drain remaining output
        drainEncoder(bufferInfo, true);
    }

    private void drainEncoder(MediaCodec.BufferInfo bufferInfo, boolean endOfStream) {
        if (endOfStream) {
            videoEncoder.signalEndOfInputStream();
        }

        while (true) {
            int outputBufferIndex = videoEncoder.dequeueOutputBuffer(bufferInfo, 10000);
            
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break;
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (muxerStarted) {
                    throw new RuntimeException("Format changed twice");
                }
                MediaFormat newFormat = videoEncoder.getOutputFormat();
                videoTrackIndex = mediaMuxer.addTrack(newFormat);
                
                if (audioTrackIndex == -1 || audioTrackIndex >= 0) {
                    mediaMuxer.start();
                    muxerStarted = true;
                }
            } else if (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = videoEncoder.getOutputBuffer(outputBufferIndex);
                if (outputBuffer != null && muxerStarted) {
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                        mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, bufferInfo);
                    }
                }

                videoEncoder.releaseOutputBuffer(outputBufferIndex, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }
    }

    private void addAudioTrack(String audioPath, float totalDurationSec) {
        try {
            MediaExtractor audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(audioPath);

            // Find audio track
            int audioTrack = -1;
            for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
                MediaFormat format = audioExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime != null && mime.startsWith("audio/")) {
                    audioTrack = i;
                    break;
                }
            }

            if (audioTrack >= 0) {
                audioExtractor.selectTrack(audioTrack);
                MediaFormat audioFormat = audioExtractor.getTrackFormat(audioTrack);
                
                if (!muxerStarted) {
                    audioTrackIndex = mediaMuxer.addTrack(audioFormat);
                    mediaMuxer.start();
                    muxerStarted = true;
                } else {
                    audioTrackIndex = mediaMuxer.addTrack(audioFormat);
                }

                // Copy audio data
                ByteBuffer buffer = ByteBuffer.allocate(256 * 1024);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                long totalDurationUs = (long) (totalDurationSec * 1000000);

                while (true) {
                    int sampleSize = audioExtractor.readSampleData(buffer, 0);
                    if (sampleSize < 0 || audioExtractor.getSampleTime() > totalDurationUs) {
                        break;
                    }

                    bufferInfo.presentationTimeUs = audioExtractor.getSampleTime();
                    bufferInfo.size = sampleSize;
                    bufferInfo.offset = 0;
                    bufferInfo.flags = audioExtractor.getSampleFlags();

                    mediaMuxer.writeSampleData(audioTrackIndex, buffer, bufferInfo);
                    audioExtractor.advance();
                }
            }

            audioExtractor.release();
        } catch (Exception e) {
            Log.e(TAG, "Error adding audio track", e);
        }
    }

    private void finishEncoding() {
        try {
            if (videoEncoder != null) {
                videoEncoder.stop();
                videoEncoder.release();
                videoEncoder = null;
            }

            if (mediaMuxer != null) {
                if (muxerStarted) {
                    mediaMuxer.stop();
                }
                mediaMuxer.release();
                mediaMuxer = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finishing encoding", e);
        }
    }

    private Bitmap loadAndScaleBitmap(File imageFile, int targetWidth, int targetHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            if (bitmap != null) {
                return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading bitmap", e);
        }
        return null;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap applyFrameOverlay(Bitmap source, Bitmap frame, int width, int height) {
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        
        // Draw source image
        canvas.drawBitmap(source, 0, 0, null);
        
        // Scale and draw frame overlay
        Bitmap scaledFrame = Bitmap.createScaledBitmap(frame, width, height, true);
        canvas.drawBitmap(scaledFrame, 0, 0, null);
        
        scaledFrame.recycle();
        return result;
    }

    private byte[] convertBitmapToYUV420(Bitmap bitmap, int width, int height) {
        int[] argb = new int[width * height];
        bitmap.getPixels(argb, 0, width, 0, 0, width, height);

        byte[] yuv = new byte[width * height * 3 / 2];
        encodeYUV420SP(yuv, argb, width, height);

        return yuv;
    }

    private void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                a = (argb[index] & 0xff000000) >> 24; // not used
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff);

                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));

                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                }

                index++;
            }
        }
    }

    public void cancel() {
        encodingComplete.set(true);
        finishEncoding();
    }
}
