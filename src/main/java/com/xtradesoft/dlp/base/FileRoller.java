/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The Class FileRoller.
 */
public class FileRoller {

    /**
     * The Class FileComparator.
     */
    private final class FileComparator implements Comparator<File> {

        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final File file1, final File file2) {

            final long diff = file2.lastModified() - file1.lastModified();
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return +1;
            } else {
                return 0;
            }
        }

    }

    /**
     * The Class FileFilter.
     */
    private final class FileFilter implements java.io.FileFilter {

        /*
         * (non-Javadoc)
         * @see java.io.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(final File file) {

            final String path = file.getAbsolutePath();
            return path.startsWith(filenameWithoutExtension) && path.endsWith(filenameExtension);
        }

    }

    /** The comparator. */
    private final FileComparator comparator;

    /** The file. */
    private File file;

    /** The filename extension. */
    private String filenameExtension;

    /** The filename without extension. */
    private String filenameWithoutExtension;

    /** The filter. */
    private final FileFilter filter;

    /**
     * Instantiates a new FileRoller.
     * 
     * @param baseFile
     *            the base file
     */
    public FileRoller(String baseFile) {

        filter = new FileFilter();
        comparator = new FileComparator();

        file = new File(baseFile);

        final String path = file.getPath();
        final String name = file.getName();
        final int index = name.indexOf('.', 1);
        if (index > 0) {
            filenameWithoutExtension = path.substring(0, path.length() - name.length() + index);
            filenameExtension = name.substring(index);
        } else {
            filenameWithoutExtension = path;
            filenameExtension = "";
        }

        file = createFile();

    }

    /**
     * Creates the file.
     * 
     * @return the file
     */
    private File createFile() {

        return new File(filenameWithoutExtension + "."
                + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date())
                + filenameExtension);
    }

    /**
     * Roll.
     * 
     * @param maxBackups
     *            the max backups
     * @return the file
     */
    public final File roll(final int maxBackups) {

        final List<File> files = Arrays.asList(file.getAbsoluteFile().getParentFile().listFiles(filter));
        if (files.size() > maxBackups) {
            Collections.sort(files, comparator);
            for (int i = maxBackups; i < files.size(); ++i) {
                files.get(i).delete();
            }
        }

        return createFile();
    }

}
