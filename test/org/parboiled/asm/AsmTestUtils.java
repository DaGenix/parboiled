/*
 * Copyright (C) 2009 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.asm;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.parboiled.common.StringUtils;
import static org.parboiled.test.TestUtils.assertEqualsMultiline;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AsmTestUtils {

    public static String getClassDump(byte[] code) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(printWriter);
        //ClassAdapter checkClassAdapter = new ClassAdapter(traceClassVisitor);
        ClassAdapter checkClassAdapter = new CheckClassAdapter(traceClassVisitor);
        ClassReader classReader;
        classReader = new ClassReader(code);
        classReader.accept(checkClassAdapter, 0);
        printWriter.flush();
        return stringWriter.toString();
    }

    public static String getMethodInstructionList(MethodNode methodNode) {
        TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor();
        methodNode.accept(traceMethodVisitor);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        traceMethodVisitor.print(printWriter);
        printWriter.flush();
        String[] lines = stringWriter.toString().split("\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = String.format("%2d %s", i, lines[i]);
        }
        return new StringBuilder()
                .append("Method '").append(methodNode.name).append("':\n")
                .append(StringUtils.join(lines, "\n"))
                .append('\n')
                .toString();
    }

    public static void assertTraceDumpEquality(@NotNull MethodNode method, String traceDump) throws Exception {
        TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor();
        // MethodAdapter checkMethodAdapter = new MethodAdapter(traceMethodVisitor);
        MethodAdapter checkMethodAdapter = new CheckMethodAdapter(traceMethodVisitor);
        method.accept(checkMethodAdapter);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        traceMethodVisitor.print(printWriter);
        printWriter.flush();

        assertEqualsMultiline(stringWriter.toString(), traceDump);
    }

    public static void verifyIntegrity(String classInternalName, byte[] classCode) {
        ClassNode generatedClassNode = new ClassNode();
        ClassReader classReader = new ClassReader(classCode);
        classReader.accept(generatedClassNode, 0);

        for (Object methodObj : generatedClassNode.methods) {
            MethodNode method = (MethodNode) methodObj;
            try {
                new Analyzer(new SimpleVerifier()).analyze(classInternalName, method);
            } catch (AnalyzerException e) {
                throw new RuntimeException(
                        "Integrity error in method '" + method.name + "' of type '" + classInternalName + "': ", e);
            }
        }
    }

}
