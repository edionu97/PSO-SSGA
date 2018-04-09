package Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FileParser {

    private int chunk;

    private char[] buffer;
    private BufferedReader reader;

    private String location;
    private StringBuilder builder = new StringBuilder("");

    private  Set<Double> set = new HashSet<>();


    private BufferedReader getReader() throws FileNotFoundException {
        return new BufferedReader(new FileReader(location));
    }

    private void getInfo(char[] string,int dim){

        for(int i = 0; i < dim; ++i){

            if(string[i] == ' '){
                if(!builder.toString().equals(""))set.add(Double.parseDouble(builder.toString().trim()));
                builder = new StringBuilder("");
                continue;
            }

            builder.append(string[i]);
        }

    }

    public FileParser(int chunkDimension, String location) throws FileNotFoundException {
        this.location = location;
        this.chunk = chunkDimension;
        buffer = new char[chunkDimension + 1];
        reader = getReader();
    }


    public Set<Double> readFile() throws IOException {


        int readed;
        while((readed= reader.read(buffer,0,chunk)) > 0 )getInfo(buffer,readed);

        if(!builder.toString().trim().equals(""))set.add(Double.parseDouble(builder.toString().trim()));

        return set;
    }

    public BufferedReader getBufferedReader(){
        return  reader;
    }
}
