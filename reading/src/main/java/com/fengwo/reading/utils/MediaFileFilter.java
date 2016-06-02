package com.fengwo.reading.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by timeloveboy on 16/5/20.
 */
public class MediaFileFilter  implements FilenameFilter {

    private String type;

    public MediaFileFilter(String tp){

        this.type=tp;

    }

    public boolean accept(File fl,String path) {
        File file = new File(path);

        String filename = file.getName();

        return filename.indexOf(type) != -1;

    }

}
