import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MultipleFileReader implements Runnable {
    Map<Character, Integer> alphabetmap;
    String filename;
    List<File> files;
    int start;
    int end;

    public MultipleFileReader(List<File> files, Map<Character, Integer> alphabetmap, int start, int end){
        this.alphabetmap=alphabetmap;
        this.files=files;
        this.start=start;
        this.end=end;
    }


    @Override
    public void run() {
        for(int i=start;i<end;i++){
            filename=files.get(i).getPath();
            try(BufferedReader bf=new BufferedReader(new FileReader(filename))){
                String line;
                while ((line=bf.readLine())!=null){
                    for (char c : line.toCharArray()) {
                        if (c>=97 && c<=122){
                            alphabetmap.put(c, alphabetmap.getOrDefault(c, 0) + 1);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
public class Main {

    public static void main(String[] args) {
        Map<Character, Integer> globalalphabetmap= new ConcurrentHashMap<>();
        if(args.length<=0){
            System.out.println("Directory Path Missing");
        } else{
            String dirPath=args[0];
            File directory;
            directory=new File(dirPath);
            List<File> files=new ArrayList<>();
            File[] tempfiles=directory.listFiles();
            for(int i=0;i<tempfiles.length;i++){
                if(tempfiles[i].isDirectory()){
                    files.addAll(Arrays.asList(tempfiles[i].listFiles()));
                }else if(tempfiles[i].getName().endsWith(".txt")){
                    files.add(tempfiles[i]);
                }
            }

            int Thread_Count=4;
            int chunkSize=files.size()/Thread_Count;
            ExecutorService executor= Executors.newFixedThreadPool(Thread_Count);
            for(int i=0;i<Thread_Count;i++){
                int start=i*chunkSize;
                int end= (i==Thread_Count-1)?files.size():(start+chunkSize);

                executor.execute(new MultipleFileReader(files, globalalphabetmap, start, end));
            }

            globalalphabetmap.forEach((key, val)-> System.out.println(key+"="+val));

            executor.shutdown();
        }
    }
}

