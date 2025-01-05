package poa.poabackdoorcleaner.util;

import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Remover {


    public static void cleanPlugins() throws IOException {
        File pluginFolder = new File("plugins");
        File[] pluginFiles = pluginFolder.listFiles((dir, name) -> name.endsWith(".jar"));

        if (pluginFiles == null || pluginFiles.length == 0) {
            System.out.println("No plugins found in the plugin folder.");
            return;
        }

        for (File pluginFile : pluginFiles) {
            File tempJar = new File(pluginFile.getParent(), pluginFile.getName() + ".tmp");
            Set<String> processedEntries = new HashSet<>();

            try (ZipFile zipFile = new ZipFile(pluginFile);
                 ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempJar))) {

                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();

                    if (processedEntries.contains(entry.getName())) {
                        continue;
                    }
                    processedEntries.add(entry.getName());
                    if (entry.getName().startsWith("javassist/")
                            || entry.getName().equals("org/bukkit/plugin/Plugin.java")
                            || entry.getName().startsWith("com/thiccindustries/debugger")) {
                        System.out.println("Removing: " + entry.getName());
                        continue;
                    }

                    zos.putNextEntry(new ZipEntry(entry.getName()));
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        zos.write(inputStream.readAllBytes());
                    }
                    zos.closeEntry();
                }
            }

            if (pluginFile.delete() && tempJar.renameTo(pluginFile)) {
                System.out.println("Cleaned: " + pluginFile.getName());
            } else {
                System.err.println("Failed to replace the original JAR file for: " + pluginFile.getName());
            }
        }
    }

    private static byte[] processClass(InputStream classInputStream) throws IOException {
        ClassReader reader = new ClassReader(classInputStream);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (name.equals("onEnable")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitTypeInsn(int opcode, String type) {
                            if (opcode == Opcodes.NEW && type.equals("com/thiccindustries/debugger/Debugger")) {
                                System.out.println("Removing new Debugger line in onEnable");
                                return;
                            }
                            super.visitTypeInsn(opcode, type);
                        }
                    };
                }
                return mv;
            }
        };
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }
}
