/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.tools.web;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

public class Viewer
extends ClassLoader {
    private String server;
    private int port;

    public static void main(String[] args2) throws Throwable {
        if (args2.length >= 3) {
            Viewer cl = new Viewer(args2[0], Integer.parseInt(args2[1]));
            String[] args22 = new String[args2.length - 3];
            System.arraycopy(args2, 3, args22, 0, args2.length - 3);
            cl.run(args2[2], args22);
        } else {
            System.err.println("Usage: java javassist.tools.web.Viewer <host> <port> class [args ...]");
        }
    }

    public Viewer(String host, int p) {
        this.server = host;
        this.port = p;
    }

    public String getServer() {
        return this.server;
    }

    public int getPort() {
        return this.port;
    }

    public void run(String classname, String[] args2) throws Throwable {
        Class<?> c = this.loadClass(classname);
        try {
            c.getDeclaredMethod("main", String[].class).invoke(null, new Object[]{args2});
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = this.findLoadedClass(name);
        if (c == null) {
            c = this.findClass(name);
        }
        if (c == null) {
            throw new ClassNotFoundException(name);
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> c = null;
        if (name.startsWith("java.") || name.startsWith("javax.") || name.equals("com.viaversion.viaversion.libs.javassist.tools.web.Viewer")) {
            c = this.findSystemClass(name);
        }
        if (c == null) {
            try {
                byte[] b = this.fetchClass(name);
                if (b != null) {
                    c = this.defineClass(name, b, 0, b.length);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return c;
    }

    protected byte[] fetchClass(String classname) throws Exception {
        byte[] b;
        URL url = new URL("http", this.server, this.port, "/" + classname.replace('.', '/') + ".class");
        URLConnection con = url.openConnection();
        con.connect();
        int size = con.getContentLength();
        InputStream s = con.getInputStream();
        if (size <= 0) {
            b = this.readStream(s);
        } else {
            int n;
            b = new byte[size];
            int len = 0;
            do {
                if ((n = s.read(b, len, size - len)) >= 0) continue;
                s.close();
                throw new IOException("the stream was closed: " + classname);
            } while ((len += n) < size);
        }
        s.close();
        return b;
    }

    private byte[] readStream(InputStream fin) throws IOException {
        byte[] buf = new byte[4096];
        int size = 0;
        int len = 0;
        do {
            if (buf.length - (size += len) > 0) continue;
            byte[] newbuf = new byte[buf.length * 2];
            System.arraycopy(buf, 0, newbuf, 0, size);
            buf = newbuf;
        } while ((len = fin.read(buf, size, buf.length - size)) >= 0);
        byte[] result = new byte[size];
        System.arraycopy(buf, 0, result, 0, size);
        return result;
    }
}

