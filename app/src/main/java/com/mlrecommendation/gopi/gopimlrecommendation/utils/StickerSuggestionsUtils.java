package com.mlrecommendation.gopi.gopimlrecommendation.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import android.os.SystemClock;


import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.TextView;
import com.mlrecommendation.gopi.gopimlrecommendation.MyApplication;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.schedulers.Schedulers;

import kotlin.io.ByteStreamsKt;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;


public class StickerSuggestionsUtils {
    private static final String TAG = "StickerSuggestionsUtils";
    private static final String STICKER_REGEX = "(?:[\\w]+:[\\w]+[!]?.png)";
    private static final String NUDGE_REGEX = "(?:nudge!|Nudge!)";
    private static final String HIKE_EMOJI_REGEX = "(?:\\(giggle\\)|\\(grin2\\)|\\(kissed\\)|\\(haha1\\)|\\(snooty2\\)|\\(sweat\\)|\\(crying2\\)|\\(kiss2\\)|\\(wornout\\)|\\(hug\\)|\\(yawn\\)|\\(smoking\\)|\\(happy2\\)|\\(stop\\)|\\(grimace1\\)|\\(scared\\)|\\(suspicious2\\)|\\(snotty\\)|\\(omg2\\)|\\(drunk2\\)|\\(what1\\)|\\(shame1\\)|\\(yuush2\\)|\\(sweat2\\)|\\(hi\\)|\\(victory1\\)|\\(cry1\\)|\\(scorn1\\)|\\(girl1\\)|\\(slow2\\)|\\(horror1\\)|\\(anger1\\)|\\(ninja\\)|\\(movie\\)|\\(study\\)|\\(wut2\\)|\\(icecream\\)|\\(meow2\\)|\\(monocle\\)|\\(music\\)|\\(angry2\\)|\\(exciting1\\)|\\(kaboom2\\)|\\(graffiti\\)|\\(amazing1\\)|\\(beer\\)|\\(hero\\)|\\(business2\\)|\\(ouch2\\)|\\(greedy1\\)|\\(xd2\\)|\\(chai\\)|\\(shocked1\\)|\\(dizzy2\\)|\\(nothing1\\)|\\(sick2\\)|\\(paisa\\)|\\(tiranga\\)|\\(chips\\)|\\(yarr2\\)|\\(coffee\\)|\\(love\\)|\\(nariyal\\)|\\(want2\\)|\\(nerd2\\)|\\(boo2\\)|\\(police\\)|\\(unhappy1\\)|\\(sleeping2\\)|\\(money1\\)|\\(happy1\\)|\\(samosa\\)|\\(disappearing2\\)|\\(beaten1\\)|\\(biscuit\\)|\\(evilish\\)|\\(metro\\)|\\(nimbu\\)|\\(struggle\\)|\\(brains2\\)|\\(noodles\\)|\\(jalebi\\)|\\(ninja2\\)|\\(music2\\)|\\(neta\\)|\\(auto\\)|\\(shaktiman\\)|\\(batti\\)|\\(tandoori\\)|\\(bad\\ egg1\\)|\\(bad\\ smile1\\)|\\(face\\ palm\\)|\\(electric\\ shock1\\)|\\(kidding\\ right2\\)|\\(red\\ heart1\\)|\\(bad\\ egg1\\)|\\(red\\ heart1\\)|\\(fake\\ smile2\\))";
    private static final String MEDIA_RECIEVED_REGEX = "(?:sent\\ you\\ (?:an\\ audio|a\\ photo|a\\ file|a\\ video))";
    private static final String STANDARD_EMOJI_REGEX = "(?:\\U0001f602|\\U0001f618|\\U0001f60a|\\U0001f48b|\\U0001f61c|\\U0001f60d|\\U0001f612|\\u263a|\\U0001f61d|\\U0001f601|\\U0001f605|\\U0001f621|\\U0001f62d|\\U0001f648|\\U0001f614|\\u2764|\\U0001f44d|\\U0001f60f|\\U0001f609|\\U0001f604|\\U0001f633|\\U0001f600|\\U0001f61b|\\U0001f61e|\\U0001f61a|\\U0001f60b|\\U0001f44a|\\U0001f603|\\U0001f64f|\\U0001f644|\\U0001f611|\\U0001f606|\\U0001f613|\\U0001f622|\\U0001f44c|\\U0001f60c|\\U0001f60e|\\U0001f623|\\U0001f62c|\\U0001f610|\\U0001f914|\\U0001f615|\\U0001f630|\\U0001f631|\\u270c|\\U0001f625|\\U0001f62a|\\U0001f620|\\U0001f64a|\\U0001f629|\\U0001f483|\\U0001f44f|\\U0001f607|\\U0001f62b|\\U0001f636|\\U0001f624|\\u2639|\\U0001f339|\\U0001f44b|\\U0001f616|\\u26bd|\\U0001f47b|\\U0001f61f|\\U0001f3fb|\\U0001f642|\\U0001f48f|\\U0001f917|\\U0001f64c|\\U0001f49e|\\U0001f637|\\U0001f628|\\U0001f496|\\U0001f444|\\U0001f513|\\U0001f446|\\U0001f449|\\U0001f3b8|\\U0001f913|\\U0001f352|\\U0001f634|\\U0001f445|\\U0001f641|\\U0001f643|\\U0001f619|\\U0001f49c|\\U0001f639|\\U0001f608|\\u270b|\\U0001f4e9|\\u2763|\\U0001f46b|\\U0001f495|\\U0001f381|\\U0001f3c3|\\U0001f62f|\\U0001f646|\\U0001f617|\\U0001f494|\\U0001f448|\\U0001f497)";
    private static final String HIKE_SYMBOLS_REGEX = "(?:\\(\\.V\\.\\)|O:-\\)|X-\\(|~:0|:-D|\\(\\*v\\*\\)|:-\\#|</3|=\\^\\.\\^=|\\*<:o\\)|O\\.o|B-\\)|:_\\(|:'\\(|\\\\:D/|\\*-\\*|:o3|\\#-o|:\\*\\)|//_\\^|>:\\)|<><|:-\\(|:\\(|:-\\(|=P|:-P|8-\\)|\\$_\\$|:->|=\\)|:-\\)|:\\)|<3|\\{\\}|:-\\||X-p|:-\\)\\*|:-\\*|:\\*|\\(-\\}\\{-\\)|XD|=D|\\)-:|\\(-:|<3|=/|:-\\)\\(-:|<:3\\)~|~,~|:-B|\\^_\\^|<l:0|:-/|=8\\)|@~\\)~~~~|=\\(|:-\\(|:\\(|:S|:-@|=O|:-o|:-\\)|:\\)|:-Q|:>|:P|:o|:-J|:-&|=-O|:-\\\\|:-E|=D|;-\\)|;\\)|\\|-O|8-\\#|:-b|:-\"|:-\\*|:d|\\^\\.\\^|\\$-\\)|:-x|:'-\\(|:-<|:\"\\)|:-0|:\"\\)|\\*-\\)|x\\()";
    private static final String OTHERS_REGEX = "(?:[^\\W\\d_](?:[^\\W\\d_]|['\\-_])+[^\\W\\d_]) # Words with apostrophes or dashes.\n"
            + "    |\n"
            + "    (?:[+\\-]?\\d+[,/.:-]\\d+[+\\-]?)  # Numbers, including fractions, decimals.\n"
            + "    |\n"
            + "    (?:[\\w_]+)                     # Words without apostrophes or dashes.\n"
            + "    |\n"
            + "    (?:[!?*&,]{1,})    ";
    private ArrayList<String> prevMsgLines = new ArrayList<>();
    private ArrayList<String> typedMsgLines = new ArrayList<>();
    private ArrayList<String> clusteredMsgLines = new ArrayList<>();
    private ArrayList<String> outputLines;


    private final String regex = STICKER_REGEX
            + "|" + NUDGE_REGEX
            + "|" + HIKE_EMOJI_REGEX
            + "|" + MEDIA_RECIEVED_REGEX
            + "|" + STANDARD_EMOJI_REGEX
            + "|" + HIKE_SYMBOLS_REGEX
            + "|" + OTHERS_REGEX;
    private Pattern MY_PATTERN;


    MappedByteBuffer mappedByteBuffer = null;
    private Context context;
    private int[] prevMessageIndices = new int[10*5000];
    private int[] typedMessageIndices = new int[14066];
    private HashMap<String, ArrayList<Float>> prevMessageEmbedding;
    private HashMap<String, ArrayList<Float>> currMessageEmbedding;
    private int numberOfprediction = 0;
    private String EOSString = "EOS";
    private String UNKString = "UNK";
//    private int prevEOSIndex = -1;
//    private int prevUNKIndex = -1;
//    private int typedEOSIndex = -1;
//    private int typedUNKIndex = -1;
    Random rand = new Random();
    //private ByteBuffer inputData = null;
    float[][] probabilities;
    boolean isNewFlow = true;
    Interpreter tflite;
    private static final float MIN_VALUE = 0.001f;
    private static StickerSuggestionsUtils instance;


    private StickerSuggestionsUtils() {
        this.context = MyApplication.Companion.getInstance();
        loadMLModel();


//        outputLines = loadTextFileFromAssets(context, "vocab.stickers");
        //inputData = ByteBuffer.allocateDirect((prevMsgLines.size()+typedMsgLines.size()));
        //inputData.order(ByteOrder.nativeOrder());
        //prevMessageEmbedding = loadEmbeddingFromFile(context,"vocab.prev" );
        //currMessageEmbedding = loadEmbeddingFromFile(context,"vocab.typed" );
    }

    public static StickerSuggestionsUtils getInstance() {
        if(instance == null){
            instance = new StickerSuggestionsUtils();
        }
        return instance;
    }



    private void loadMLModel() {
        Completable.create((CompletableEmitter emitter) -> {
            MappedByteBuffer mappedByteBuffer = null;
            try {
                MY_PATTERN = Pattern.compile(regex);
                long currentUsedMemoryInMB = CommonUtils.getCurrentUsedMemoryInMB();
                Interpreter.Options options = (new Interpreter.Options());
                mappedByteBuffer = loadModelFile(context.getAssets(), "latest_tflite_8.tflite");
                GpuDelegate delegate = new GpuDelegate();
//                TfLiteDelegate obj
                options.addDelegate(delegate);
                tflite = new Interpreter(mappedByteBuffer, options);
//                prevMsgLines = loadTextFileFromAssets(context, "vocab.prev");
//                typedMsgLines = loadTextFileFromAssets(context, "vocab.typed");
//                clusteredMsgLines = loadTextFileFromAssets(context, "cluster_map.out");
                MyApplication.Companion.getInstance().showToast("TFLite LOADING SUCCESS*** memory "+ (CommonUtils.getCurrentUsedMemoryInMB() - currentUsedMemoryInMB));
            } catch (Throwable e) {
                e.printStackTrace();
                MyApplication.Companion.getInstance().showToast("TFLite LOADING GPU FAILED*** trying CPU fallback with " + e.getMessage());


                try {
                    tflite = new Interpreter(mappedByteBuffer, new Interpreter.Options());
                    MyApplication.Companion.getInstance().showToast("TFLite LOADING SUCCESS*** memory ");
                } catch (Exception e1) {
                    e.printStackTrace();
                    MyApplication.Companion.getInstance().showToast("TFLite ALL LOADING FAILED*** with " + e.getMessage());
                }

            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe(RxUtils.getCompletableObserver());
    }

    public float mAverageTimeTaken = 0, mOverAllAverage = 0;
    public float numberOfIterations = 1;

    public void getRecStickers(String lastReceivedMsg, String messageTyped, TextView showResultsTv, TextView overallAverageTv) {
       /* ArrayList<Pair<String,String>> pairArrayList = new ArrayList<>(10);
        for (int i = 0; i < 20; i++) {
            String outputLine = outputLines.get(new Random().nextInt(4036));
            String[] strings = outputLine.split(":");
            Pair<String,String> stickerIDsPair = new Pair<>(strings[0],strings[1]);
            pairArrayList.add(stickerIDsPair);

        }
        return pairArrayList;*/
        Completable.create((CompletableEmitter emitter) -> {
        if (tflite == null) {
//            Toast.makeText(context,"TFLite loadiing faiiled", Toast.LENGTH_SHORT).show();
            return;
        }
        try{

            String lastMsgMessageText = "last";
            String typedMessage = messageTyped;
            ArrayList<String> prevMsgTokens = getTokens(lastMsgMessageText);
            ArrayList<String> currentMsgTokens = getTokens(typedMessage);
//            probabilities = new float[1][clusteredMsgLines.size()];
//            float[][] probabilities = new float[1][49999];
//            float[][] probabilities = new float[1][9936];
//            float[][] probabilities = new float[1][99999];
            float[][] probabilities = new float[2][20];
//            getPrevMsgInput(prevMsgTokens);
//            getTypedMsgInput(currentMsgTokens);
            Object[] objects = convertCNNQuantizeInput(prevMsgTokens, currentMsgTokens);
//            final int[][] prevAry = new int[1][50000];
//            final int[][] typedAry = new int[1][50000];
           /* for (int i = 0; i < prevAry[0].length; i++) {
                prevAry[0][i] = 1;
            }
            for (int i = 0; i < typedAry[0].length; i++) {
                typedAry[0][i] = 1;
            }*/
//            Object[] objects = new Object[]{prevAry, typedAry};  // use tflite.getInputTensor(0) -> see shapeCopy which shows size 0 = 1 , 1 = 14066 ...

//            PriorityQueue<PriorityIndexClass> queue = new PriorityQueue<>(100);
//            ByteBuffer inputData = ConvertInputtoByteBufferQuantize(prevMsgTokens,currentMsgTokens);
            //ByteBuffer input = ConvertInputtoByteBuffer(prevMsgTokens,currentMsgTokens );
            //ByteBuffer input = ConvertInputtoByteBufferDense(prevMsgTokens,currentMsgTokens );
            //float[][] confidence = new float[1][1];

//            int index = tflite.getInputIndex("Model/data_x_prevmsg");
//            Tensor tenssor = tflite.getInputTensor(0-);
//            int[][] floats1 = new int[1][50000];
//            int[][] floatsOther = new int[1][14066];
//
//            Object[] objects = new Object[]{floats1, floatsOther};
            HashMap<Integer, Object> map = new HashMap<>();
            map.put(0, probabilities);


//            Log.d(TAG, "My tensor: " + tenssor.toString());
//            tflite.run(concatenate(prevMessageIndices, typedMessageIndices), probabilities);
//            tflite.run(random(null,null), probabilities);
            float averageTimeTaken = 0;
            for (float i = 1; i < 100f; i++) {
                long startTime = SystemClock.uptimeMillis();
                tflite.runForMultipleInputsOutputs(objects, map);
//            tflite.run(random(prevMsgTokens,currentMsgTokens), probabilities);
                averageTimeTaken = (averageTimeTaken * ((i-1)/i)) + ((SystemClock.uptimeMillis() - startTime)/i);
//                averageTimeTaken = (averageTimeTaken * (i-1) + (SystemClock.uptimeMillis() - startTime))/i;
            }
//            Log.d(TAG, "Timecost to run model inference: " + (endTime - startTime));
//            numberOfprediction++;
            //tflite.runForMultipleInputsOutputs(new int[][]{prevMessageIndices,typedMessageIndices},outputMap);
            //float[][] outputArray = (float[][]) outputMap.get(1);
//            for (float v : probabilities[0]) {
//                Log.e(TAG,v+"");
//            }
           /* for (int i = 0; i < 10; i++) {
                Log.e(TAG, "i: " + i + " " + probabilities[0][i]);
            }*/
//            tflite.close();
//            Log.e(TAG, "Number of prediction yet " + numberOfprediction);
//            Toast.makeText(context," Success with "+ probabilities[0][1] + " in time " + (endTime - startTime) ,Toast.LENGTH_SHORT).show();
//            Toast.makeText(context," Success with "+ probabilities[0][1] + " in 1000 iterations time " + averageTimeTaken ,Toast.LENGTH_SHORT).show();
            MyApplication.Companion.getInstance().showToast(" Success with "+ probabilities[0][1] + " in 100 iterations time " + averageTimeTaken);
            mAverageTimeTaken = averageTimeTaken;
            mOverAllAverage = (mOverAllAverage * ((numberOfIterations-1)/numberOfIterations)) + (averageTimeTaken/numberOfIterations);
            showResultsTv.post(() -> {
                final String output = showResultsTv.getText().toString() + ":::" + mAverageTimeTaken + ":::\t\t";
                showResultsTv.setText(output);
                overallAverageTv.setText("Overall Average: " + mOverAllAverage);
            });
            numberOfIterations++;
        }
        catch (Exception e) {
            e.printStackTrace();
            MyApplication.Companion.getInstance().showToast(" crashed with "+ e.getMessage());
        }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe(RxUtils.getCompletableObserver());
    }

    private Object[] convertQuantizeInput(ArrayList<String> prevMsgTokens, ArrayList<String> currentMsgTokens) {
        /*int[][] prevInputAry = new int[1][prevMsgLines.size()];
        int[][] typingInputAry = new int[1][typedMsgLines.size()];*/
        int[][] prevInputAry = new int[1][50000];
        int[][] typingInputAry = new int[1][50000];

       /* for (String prevEnteredToken: prevMsgTokens){
            final int foundIndex = prevMsgLines.indexOf(prevEnteredToken);
            if ((foundIndex > -1)) {
                prevInputAry[0][foundIndex] = 1;
            }
        }

        for (String item: currentMsgTokens){
            final int foundIndex = typedMsgLines.indexOf(item);
            if ((foundIndex > -1)) {
                typingInputAry[0][foundIndex] = 1;
            }
        }*/

        return new Object[]{prevInputAry, typingInputAry};
    }

    private Object[] convertCNNQuantizeInput(ArrayList<String> prevMsgTokens, ArrayList<String> currentMsgTokens) {
        int[][] prevInputAry = new int[1][50000];
        final int MAX_CHAR_LENGTH = 10;
        int[][] typingInputAry = new int[1][MAX_CHAR_LENGTH];
        int[] charLenArray = new int[]{MAX_CHAR_LENGTH};

        for (int i = 0; i < MAX_CHAR_LENGTH; i++) {
            typingInputAry[0][i] = i;
        }
        return new Object[]{prevInputAry, typingInputAry, charLenArray};
    }

    private ByteBuffer ConvertInputtoByteBufferDense(ArrayList<String> prevMsgTokens, ArrayList<String> currentMsgTokens) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * (currentMsgTokens.size() + prevMsgTokens.size()));
        byteBuffer.order(ByteOrder.nativeOrder());
        ArrayList<Float> prevEmbedd = new ArrayList<Float>();
        ArrayList<Float> prevEmbeddEOS = prevMessageEmbedding.get(EOSString);
        for (int i = 0; i < prevEmbeddEOS.size(); i++){
            prevEmbedd.add(prevEmbeddEOS.get(i));
        }
        int maxLenPrev = Math.min(prevMsgTokens.size(), 10);
        for (int i = 0; i < maxLenPrev; i++) {
            String prevToken = prevMsgTokens.get(i).toLowerCase();
            if (prevMessageEmbedding.containsKey(prevToken)) {
                prevEmbedd = addVector(prevEmbedd, prevMessageEmbedding.get(prevToken));
            } else {
                prevEmbedd = addVector(prevEmbedd, prevMessageEmbedding.get(UNKString));
            }
        }
        if (prevMsgTokens.size() > 0){
            divideVectorByConstant(prevEmbedd, prevMsgTokens.size());
        }
        for (int i = 0; i < prevEmbedd.size(); i++){
            byteBuffer.putFloat(prevEmbedd.get(i));
        }

        Log.e(TAG, "Length of currentMsgTokens: " + currentMsgTokens.size());
        ArrayList<Float> typeEmbedd = new ArrayList<Float>();
        ArrayList<Float> typeEmbeddEOS = currMessageEmbedding.get(EOSString);
        for (int i = 0; i < typeEmbeddEOS.size(); i++){
            typeEmbedd.add(typeEmbeddEOS.get(i));
        }
        int maxLenTyped = Math.min(currentMsgTokens.size(), 5);
        for (int i = 0; i < maxLenTyped; i++) {
            String prevToken = currentMsgTokens.get(i).toLowerCase();
            if (currMessageEmbedding.containsKey(prevToken)) {
                typeEmbedd = addVector(typeEmbedd, currMessageEmbedding.get(prevToken));
            } else {
                typeEmbedd = addVector(typeEmbedd, currMessageEmbedding.get(UNKString));
            }
        }

        if (currentMsgTokens.size() > 0){
            divideVectorByConstant(typeEmbedd, currentMsgTokens.size());
        }

        for (int i = 0; i < typeEmbedd.size(); i++){
            byteBuffer.putFloat(typeEmbedd.get(i));
        }

//        Log.e(TAG, "Converted byte Array" + prevMsgTokens.size());
//        for (int i = 0; i < 200; i++) {
//            byteBuffer.putFloat(rand.nextFloat());
//        }
        return byteBuffer;
    }

    private ArrayList<Float> addVector(ArrayList<Float> inp1, ArrayList<Float> inp2){
        for(int i = 0; i < inp2.size();i++){
            float temp = inp1.get(i) + inp2.get(i);
            inp1.set(i,temp);
        }
        return  inp1;
    }

    private ArrayList<Float> divideVectorByConstant(ArrayList<Float> inp1, int div){
        for(int i = 0; i < inp1.size();i++){
            float temp = inp1.get(i)/div;
            inp1.set(i,temp);
        }
        return  inp1;
    }

    private int[][] random(ArrayList<String> prevMsgTokens, ArrayList<String> currentMsgTokens){
       /* ByteBuffer inputData = ByteBuffer.allocateDirect((prevMsgLines.size()));
//        ByteBuffer inputData = ByteBuffer.allocateDirect((typedMsgLines.size()));
        inputData.order(ByteOrder.nativeOrder());

        //inputData.rewind();
        long startTime = SystemClock.uptimeMillis();
//        for(int i = 0; i < (prevMsgLines.size()+typedMsgLines.size()); i++){
//            final int v = 1;
//            inputData.put((byte) (v & 0xFF));
//        }
        for (String prevMsgToken : prevMsgLines) {
            int tokenIndex = prevMsgTokens.indexOf(prevMsgToken);
            if (tokenIndex != -1) {
                final int v = 1;
                inputData.put((byte) (v & 0xFF));
            }
            else{
                final int v = 0;
                inputData.put((byte) (v & 0xFF));
            }
        }*/

//        float[] floats = new float[prevMsgLines.size()];
        int[][] floats1 = new int[1][50000];
        int[][] floatsOther = new int[1][14066];

        return floats1;
    }

    private ByteBuffer ConvertInputtoByteBufferQuantize(ArrayList<String> prevMsgTokens, ArrayList<String> currentMsgTokens){
        ByteBuffer inputData = ByteBuffer.allocateDirect(4*(prevMsgLines.size()));
//        ByteBuffer inputData = ByteBuffer.allocateDirect((typedMsgLines.size()));
        inputData.order(ByteOrder.nativeOrder());

        //inputData.rewind();
        long startTime = SystemClock.uptimeMillis();
//        for(int i = 0; i < (prevMsgLines.size()+typedMsgLines.size()); i++){
//            final int v = 1;
//            inputData.put((byte) (v & 0xFF));
//        }
        for (String prevMsgToken : prevMsgLines) {
            int tokenIndex = prevMsgTokens.indexOf(prevMsgToken);
            if (tokenIndex != -1) {
                final int v = 1;
                inputData.put((byte) (v & 0xFF));
            }
            else{
                final int v = 0;
                inputData.put((byte) (v & 0xFF));
            }
        }
        /*for (String typedMsgToken : typedMsgLines) {
            int tokenIndex = currentMsgTokens.indexOf(typedMsgToken);
            if (tokenIndex != -1) {
                final int v = 1;
                inputData.put((byte) (v & 0xFF));
            }
            else{
                final int v = 0;
                inputData.put((byte) (v & 0xFF));
            }
        }*/
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime));
        return inputData;
    }

    /*private ByteBuffer ConvertInputtoByteBuffer(ArrayList<String> prevMsgTokens, ArrayList<String> currentMsgTokens){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*15);
        //byteBuffer.order(ByteOrder.nativeOrder());
        Log.e(TAG, "Length of PrevMsgToken: " + prevMsgTokens.size());
        for (int i = 0; i < 10; i++) {
            if (i < prevMsgTokens.size()) {
                int tokenIndex = prevMsgLines.indexOf(prevMsgTokens.get(i).toLowerCase());
                if (tokenIndex != -1) {
                    byteBuffer.putInt(tokenIndex);
                } else {
                    byteBuffer.putInt(4999);
                }
            }
            else{
                byteBuffer.putInt(4998);
            }
        }
        Log.e(TAG, "Length of currentMsgTokens: " + currentMsgTokens.size());
        for (int i = 0; i < 5; i++) {
            if (i < currentMsgTokens.size()) {
                int tokenIndex = typedMsgLines.indexOf(currentMsgTokens.get(i).toLowerCase());
                if (tokenIndex != -1) {
                    byteBuffer.putInt(tokenIndex);
                } else {
                    byteBuffer.putInt(4999);
                }
            }
            else{
                byteBuffer.putInt(4998);
            }
        }
        Log.e(TAG, "Converted byte Array" + prevMsgTokens.size());
        return byteBuffer;
    }*/

    //@Override
    public void close() {
        tflite.close();
        tflite = null;
    }

    private void getTypedMsgInput(ArrayList<String> currentMsgTokens) {
        for (int i = 0; i < typedMessageIndices.length; i++) {
            typedMessageIndices[i] = 4998;
        }
        int max_len = Math.min(currentMsgTokens.size(),5);
        Log.e(TAG, "Length of currentMsgTokens: " + currentMsgTokens.size());
        for (int i = 0; i < max_len; i++) {
            int tokenIndex = typedMsgLines.indexOf(currentMsgTokens.get(i).toLowerCase());
            if (tokenIndex != -1) {
                typedMessageIndices[i] = tokenIndex;
            } else {
                typedMessageIndices[i] = 4999;
            }
        }
//        for (int typedMessageIndex : typedMessageIndices) {
//            Log.d(TAG, "typedMessageIndex: " + typedMessageIndex);
//        }
    }

    private  void getPrevMsgInputFixed(ArrayList<String> prevMsgTokens){
        for (int i = 0; i < prevMessageIndices.length; i++) {
            prevMessageIndices[i] = 4998;
        }
        int max_len = Math.min(prevMsgTokens.size(),10);
        Log.e(TAG, "Length of PrevMsgToken: " + prevMsgTokens.size());
        for (int i = 0; i < max_len; i++) {
            int tokenIndex = prevMsgLines.indexOf(prevMsgTokens.get(i).toLowerCase());
            if (tokenIndex != -1) {
                prevMessageIndices[i] = tokenIndex;
            } else {
                prevMessageIndices[i] = 4999;
            }
        }
        for (int typedMessageIndex : prevMessageIndices) {
            Log.d(TAG, "prevMessageIndex: " + typedMessageIndex);
        }
    }

    private void getPrevMsgInput(ArrayList<String> prevMsgTokens) {
        for (String prevMsgToken : prevMsgTokens) {
            int tokenIndex = prevMsgLines.indexOf(prevMsgToken.toLowerCase());
            if (tokenIndex != -1) {
                Log.d(TAG, "prevMessageIndex: " + tokenIndex);
                prevMessageIndices[tokenIndex] = 1;
            }
        }
       /* for (int prevMessageIndex : prevMessageIndices) {
            if (prevMessageIndex!=0) {
                Log.d(TAG,"prevMessageIndex: "+prevMessageIndex);
            }
        }*/
    }

    private ArrayList<String> getTokens(String msg) {
        ArrayList<String> tokens = new ArrayList<>();
        //String testStr = "hehe.hehe.png nudge! hayhay sent you a video " ;

        Matcher m = MY_PATTERN.matcher(msg);
        while (m.find()) {
//            Log.d(TAG, "Start index: " + m.start());
//            Log.d(TAG, "End index: " + m.end() + " ");
//            Log.d(TAG, m.group());
            tokens.add(m.group());
        }
        String[] plainTextTokens = msg.replaceAll(regex, " ").split(" ");
        for (String plainTextToken : plainTextTokens) {
            tokens.add(plainTextToken);
        }
        return tokens;
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d(TAG, "TfLite model Loaded");
        MappedByteBuffer tfLiteMappedBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        fileDescriptor.close();
        return tfLiteMappedBuffer;
    }

    private ByteBuffer loadModelFileByteBuffer(AssetManager assetManager, String modelPath) throws IOException {
        InputStream inputStream = assetManager.open(modelPath);
//        FileInputStream inputStream = new FileInputStream(new Inpu);
//        new ByteArrayInputStream(inputStream.re)
        return ByteBuffer.wrap(ByteStreamsKt.readBytes(inputStream));
//        ByteBuffer.wrap(inputStream.re)
    }


    public ArrayList<String> loadTextFileFromAssets(Context context, String fileName) {
        ArrayList<String> lables = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                lables.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return lables;
    }

    public HashMap<String, ArrayList<Float>> loadEmbeddingFromFile(Context context, String fileName) {
        HashMap<String, ArrayList<Float>> MessageEmbedding = new HashMap<String, ArrayList<Float>>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                // Replace it with embedding fro file
                ArrayList<Float> temp_embedd = new ArrayList<Float>();
                for (int i = 0; i < 100; i++){
                    temp_embedd.add(rand.nextFloat());
                }
                MessageEmbedding.put(mLine, temp_embedd);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return MessageEmbedding;
    }

    public int[] concatenate(int[] a, int[] b) {
        int aLen = a.length;
        int bLen = b.length;
        int[] c = new int[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
