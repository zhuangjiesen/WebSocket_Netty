package com.jason.util;

import com.dragsun.websocket.client.WebSocketSession;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 英文识别工具类
 * @Date: Created in 2018/5/10
 */
public class GoogleSpeechToTextUtil {



    public static void streamingRecognizeData(byte[] data) throws Exception, IOException {

        SpeechSettings.Builder builder = SpeechSettings.newBuilder();
        builder.setCredentialsProvider(new CredentialsProvider() {
            @Override
            public Credentials getCredentials() throws IOException {
                String jsonPath = "/Users/zhuangjiesen/netease/projects/dev/nmtp/google/google/xxxxxxxx.json";
                File file = new File(jsonPath);
                FileInputStream fins = new FileInputStream(file);
                GoogleCredentials credentials = GoogleCredentials.fromStream(fins);
                return credentials;
            }
        });
        SpeechSettings speechSettings = builder.build();
        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        try (SpeechClient speech = SpeechClient.create(speechSettings)) {

            // Configure request with local raw PCM audio
            RecognitionConfig recConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .build();
            StreamingRecognitionConfig config = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recConfig)
                    .build();

            class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {
                private final SettableFuture<List<T>> future = SettableFuture.create();
                private final List<T> messages = new java.util.ArrayList<T>();

                @Override
                public void onNext(T message) {
                    messages.add(message);
                }

                @Override
                public void onError(Throwable t) {
                    future.setException(t);
                }

                @Override
                public void onCompleted() {
                    future.set(messages);
                }

                // Returns the SettableFuture object to get received messages / exceptions.
                public SettableFuture<List<T>> future() {
                    return future;
                }
            }

            ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver =
                    new ResponseApiStreamingObserver<>();

            BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable =
                    speech.streamingRecognizeCallable();

            ApiStreamObserver<StreamingRecognizeRequest> requestObserver =
                    callable.bidiStreamingCall(responseObserver);

            // The first request must **only** contain the audio configuration:
            requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(config)
                    .build());

            // Subsequent requests must **only** contain the audio data.
            requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(ByteString.copyFrom(data))
                    .build());

            // Mark transmission as completed after sending the data.
            requestObserver.onCompleted();

            long start = System.currentTimeMillis();
            List<StreamingRecognizeResponse> responses = responseObserver.future().get();
            long time = System.currentTimeMillis() - start;
            System.out.println(" time : " + time);

            for (StreamingRecognizeResponse response : responses) {
                // For streaming recognize, the results list has one is_final result (if available) followed
                // by a number of in-progress results (if iterim_results is true) for subsequent utterances.
                // Just print the first result here.
                StreamingRecognitionResult result = response.getResultsList().get(0);
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcript : %s\n", alternative.getTranscript());
            }
        }
    }


    /*
     * 解析本地文件
     * @author zhuangjiesen
     * @date 2018/5/10 上午10:44
     * @param
     * @return
     */
    public static void recognizedLocalData(byte[] data) throws Exception {
        // Instantiates a client
        SpeechSettings.Builder builder = SpeechSettings.newBuilder();
        builder.setCredentialsProvider(new CredentialsProvider() {
            @Override
            public Credentials getCredentials() throws IOException {
                String jsonPath = "/Users/zhuangjiesen/netease/projects/dev/nmtp/google/google/xxxxx.json";
                File file = new File(jsonPath);
                FileInputStream fins = new FileInputStream(file);
                GoogleCredentials credentials = GoogleCredentials.fromStream(fins);
                return credentials;
            }
        });
        SpeechSettings speechSettings = builder.build();
        try (SpeechClient speechClient = SpeechClient.create(speechSettings)) {
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setEnableWordTimeOffsets(true)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            long start = System.currentTimeMillis();
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();
            long time = System.currentTimeMillis() - start;
            System.out.println(" time : " + time );

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
            System.out.println("============================================");

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s\n", alternative.getTranscript());
                for (WordInfo wordInfo : alternative.getWordsList()) {
                    System.out.println(wordInfo.getWord());
                    System.out.printf("\t%s.%s sec - %s.%s sec\n",
                            wordInfo.getStartTime().getSeconds(),
                            wordInfo.getStartTime().getNanos() / 100000000,
                            wordInfo.getEndTime().getSeconds(),
                            wordInfo.getEndTime().getNanos() / 100000000);
                }
            }
        }
    }







    /*
     * 解析本地文件
     * @author zhuangjiesen
     * @date 2018/5/10 上午10:44
     * @param
     * @return
     */
    public static void recognizedLocalFile(String fileName) throws Exception {
        // Instantiates a client
        SpeechSettings.Builder builder = SpeechSettings.newBuilder();
        builder.setCredentialsProvider(new CredentialsProvider() {
            @Override
            public Credentials getCredentials() throws IOException {
                String jsonPath = "/Users/zhuangjiesen/netease/projects/dev/nmtp/google/google/xxxxxxxx-69538a10bb2d.json";
                File file = new File(jsonPath);
                FileInputStream fins = new FileInputStream(file);
                GoogleCredentials credentials = GoogleCredentials.fromStream(fins);
                return credentials;
            }
        });
        SpeechSettings speechSettings = builder.build();
        try (SpeechClient speechClient = SpeechClient.create(speechSettings)) {
            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
//                    .setSampleRateHertz(8000)
                    .setEnableWordTimeOffsets(true)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
            System.out.println("============================================");

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s\n", alternative.getTranscript());
                for (WordInfo wordInfo : alternative.getWordsList()) {
                    System.out.println(wordInfo.getWord());
                    System.out.printf("\t%s.%s sec - %s.%s sec\n",
                            wordInfo.getStartTime().getSeconds(),
                            wordInfo.getStartTime().getNanos() / 100000000,
                            wordInfo.getEndTime().getSeconds(),
                            wordInfo.getEndTime().getNanos() / 100000000);
                }
            }
        }
    }




    /*
     *
     * 异步解析
     * @author zhuangjiesen
     * @date 2018/5/10 上午10:44
     * @param
     * @return
     */
    public static void asyncRecognizeWords(String fileName) throws Exception {
        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        SpeechSettings.Builder builder = SpeechSettings.newBuilder();
        builder.setCredentialsProvider(AppCredentialsProviderFactory.newCredentialsProvider());
        SpeechSettings speechSettings = builder.build();
        try (SpeechClient speech = SpeechClient.create(speechSettings)) {

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Configure remote file request for Linear16
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
//                    .setSampleRateHertz(16000)
//                    .setSampleRateHertz(32000)
//                    .setSampleRateHertz(44100)
                    .setSampleRateHertz(8000)
                    .setEnableWordTimeOffsets(true)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Use non-blocking call for getting file transcription
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(config, audio);
            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s\n", alternative.getTranscript());

                String startTime = null;
                String endTime = null;
                if (alternative.getWordsList() != null && alternative.getWordsList().size() > 0) {
                    int start = 0 ;
                    int end = alternative.getWordsList().size() - 1;
                    WordInfo startWord = alternative.getWordsList().get(start);
                    WordInfo endWord = alternative.getWordsList().get(end);


                    System.out.printf("\t%s.%s sec - %s.%s sec\n",
                            startWord.getStartTime().getSeconds(),
                            startWord.getStartTime().getNanos() / 100000000,
                            endWord.getEndTime().getSeconds(),
                            endWord.getEndTime().getNanos() / 100000000);
                }

                System.out.println("");
                System.out.println("==================");
                for (WordInfo wordInfo : alternative.getWordsList()) {
                    System.out.println(wordInfo.getWord());

                    System.out.printf("\t%s.%s sec - %s.%s sec\n",
                            wordInfo.getStartTime().getSeconds(),
                            wordInfo.getStartTime().getNanos() / 100000000,
                            wordInfo.getEndTime().getSeconds(),
                            wordInfo.getEndTime().getNanos() / 100000000);
                }
            }
        }
    }




    /*
     *
     * 异步解析
     * @author zhuangjiesen
     * @date 2018/5/10 上午10:44
     * @param
     * @return
     */
    public static void asyncRecognizeWordsData(byte[] data) throws Exception {
        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        SpeechSettings.Builder builder = SpeechSettings.newBuilder();
        builder.setCredentialsProvider(AppCredentialsProviderFactory.newCredentialsProvider());
        SpeechSettings speechSettings = builder.build();
        try (SpeechClient speech = SpeechClient.create(speechSettings)) {
            ByteString audioBytes = ByteString.copyFrom(data);

            // Configure remote file request for Linear16
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .setEnableWordTimeOffsets(true)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Use non-blocking call for getting file transcription
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(config, audio);
            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s\n", alternative.getTranscript());

                String startTime = null;
                String endTime = null;
                if (alternative.getWordsList() != null && alternative.getWordsList().size() > 0) {
                    int start = 0 ;
                    int end = alternative.getWordsList().size() - 1;
                    WordInfo startWord = alternative.getWordsList().get(start);
                    WordInfo endWord = alternative.getWordsList().get(end);


                    System.out.printf("\t%s.%s sec - %s.%s sec\n",
                            startWord.getStartTime().getSeconds(),
                            startWord.getStartTime().getNanos() / 100000000,
                            endWord.getEndTime().getSeconds(),
                            endWord.getEndTime().getNanos() / 100000000);
                }

                System.out.println("");
                System.out.println("==================");
                for (WordInfo wordInfo : alternative.getWordsList()) {
                    System.out.println(wordInfo.getWord());

                    System.out.printf("\t%s.%s sec - %s.%s sec\n",
                            wordInfo.getStartTime().getSeconds(),
                            wordInfo.getStartTime().getNanos() / 100000000,
                            wordInfo.getEndTime().getSeconds(),
                            wordInfo.getEndTime().getNanos() / 100000000);
                }
            }
        }
    }






    /*
     *
     * 异步解析
     * @author zhuangjiesen
     * @date 2018/5/10 上午10:44
     * @param
     * @return
     */
    public static void asyncRecognizeWordsData(byte[] data , WebSocketSession webSocketSession) throws Exception {
        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        SpeechSettings.Builder builder = SpeechSettings.newBuilder();
        builder.setCredentialsProvider(AppCredentialsProviderFactory.newCredentialsProvider());
        SpeechSettings speechSettings = builder.build();
        try (SpeechClient speech = SpeechClient.create(speechSettings)) {
            ByteString audioBytes = ByteString.copyFrom(data);

            // Configure remote file request for Linear16
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .setEnableWordTimeOffsets(true)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Use non-blocking call for getting file transcription
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(config, audio);
            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(500);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                if (alternative.getWordsList() != null && alternative.getWordsList().size() > 0) {
                    int start = 0 ;
                    int end = alternative.getWordsList().size() - 1;
                    WordInfo startWord = alternative.getWordsList().get(start);
                    WordInfo endWord = alternative.getWordsList().get(end);



                    String text = String.format(" %s.%s - %s.%s - %s \n",
                            DateUtil.secToTime(startWord.getStartTime().getSeconds()),
                            startWord.getStartTime().getNanos() / 100000000,
                            DateUtil.secToTime(endWord.getEndTime().getSeconds()),
                            endWord.getEndTime().getNanos() / 100000000 ,
                            alternative.getTranscript());

                    System.out.printf(text);
                    webSocketSession.sendMessage(new TextWebSocketFrame(text));
                }
            }


            System.out.println("--------------");

            webSocketSession.sendMessage(new TextWebSocketFrame("--------------"));
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                if (alternative.getWordsList() != null && alternative.getWordsList().size() > 0) {
                    for (WordInfo wordInfo : alternative.getWordsList()) {
                        System.out.println(wordInfo.getWord());
                        webSocketSession.sendMessage(new TextWebSocketFrame(wordInfo.getWord()));

                        String text = String.format("\t%s.%s sec - %s.%s sec\n",
                                wordInfo.getStartTime().getSeconds(),
                                wordInfo.getStartTime().getNanos() / 100000000,
                                wordInfo.getEndTime().getSeconds(),
                                wordInfo.getEndTime().getNanos() / 100000000);
                        webSocketSession.sendMessage(new TextWebSocketFrame(text));

                        System.out.printf("\t%s.%s sec - %s.%s sec\n",
                                wordInfo.getStartTime().getSeconds(),
                                wordInfo.getStartTime().getNanos() / 100000000,
                                wordInfo.getEndTime().getSeconds(),
                                wordInfo.getEndTime().getNanos() / 100000000);
                    }
                }
            }

        }
    }





}
