package com.qeaml.plugins.cmpeco;

import java.io.File;

public class Utils {
    public static Utils utils = new Utils();

    public Utils() {
    }

    public String combinePath(String... elems) {
        return String.join(File.separator, elems);
    }

    public boolean ensureFolder(String path) {
        var f = new File(path);
        if (f.exists() && f.isDirectory())
            return true;
        return f.mkdirs();
    }
}
