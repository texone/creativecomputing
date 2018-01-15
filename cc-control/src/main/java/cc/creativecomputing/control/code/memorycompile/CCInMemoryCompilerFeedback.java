package cc.creativecomputing.control.code.memorycompile;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

import cc.creativecomputing.core.logging.CCLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final public class CCInMemoryCompilerFeedback {

    final public boolean success;
    final public List<CompilerMessage> messages = new ArrayList<>();

    public CCInMemoryCompilerFeedback(final Boolean success, final DiagnosticCollector<JavaFileObject> diagnostics) {

        this.success = success != null && success;
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            messages.add(new CompilerMessage(diagnostic));
        }
    }

    public String toString() {

        final StringBuilder sb = new StringBuilder();

        sb.append("SUCCESS: ").append(success).append('\n');
        final int iTop = messages.size();
        for (int i = 0; i < iTop; i++) {
            sb.append("\n[MESSAGE ").append(i + 1).append(" OF ").append(iTop).append("]\n\n");
            // sb.append(messages.get(i).toString()).append("\n");
            // sb.append(messages.get(i).toStringForList()).append("\n");
            sb.append(messages.get(i).toStringForDebugging()).append("\n");
        }
        return sb.toString();
    }

    final public static class CompilerMessage {

        final public Diagnostic<? extends JavaFileObject> compilerInfo;

        final public String typeOfProblem;
        final public String typeOfProblem_forDebugging;

        final public String multiLineMessage;

        public int lineNumber;
        public int columnNumber;

        final public int textHighlightPos_lineStart;
        final public int textHighlightPos_problemStart;
        final public int textHighlightPos_problemEnd;

        final public String sourceCode;
        final public String codeOfConcern;
        final public String codeOfConcernLong;

        CompilerMessage(final Diagnostic<? extends JavaFileObject> diagnostic) {

            final JavaFileObject sourceFileObject = diagnostic.getSource();
            String sourceCodePreliminary = null;
            if (sourceFileObject instanceof SimpleJavaFileObject) {
                final SimpleJavaFileObject simpleSourceFileObject = (SimpleJavaFileObject) sourceFileObject;

                try {
                    final CharSequence charSequence = simpleSourceFileObject.getCharContent(false);
                    sourceCodePreliminary = charSequence.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sourceCodePreliminary == null) {
                sourceCode = "[SOURCE CODE UNAVAILABLE]";
            } else {
                sourceCode = sourceCodePreliminary;
            }

            compilerInfo = diagnostic;

            typeOfProblem = diagnostic.getKind().name();
            typeOfProblem_forDebugging = "toString() = " + diagnostic.getKind().toString() + "; name() = " + typeOfProblem;

            try{
	            lineNumber = (int) compilerInfo.getLineNumber();
	            columnNumber = (int) compilerInfo.getColumnNumber();
            }catch(Exception e){
            	lineNumber = 0;
            	columnNumber = 0;
            }

            final int sourceLen = sourceCode.length();
            textHighlightPos_lineStart = (int) Math.min(Math.max(0, diagnostic.getStartPosition()), sourceLen);
            textHighlightPos_problemStart = (int) Math.min(Math.max(0, diagnostic.getPosition()), sourceLen);
            textHighlightPos_problemEnd = (int) Math.min(Math.max(0, diagnostic.getEndPosition()), sourceLen);

            final StringBuilder reformattedMessage = new StringBuilder();
            final String message = diagnostic.getMessage(Locale.US);
            final int messageCutOffPosition = message.indexOf("location:");
            final String[] messageParts;
            if (messageCutOffPosition >= 0) {
                messageParts = message.substring(0, messageCutOffPosition).split("\n");
            } else {
                messageParts = message.split("\n");
            }
            for (String s : messageParts) {
                String s2 = s.trim();
                if (s2.length() > 0) {
                    boolean lengthChanged;
                    do {
                        final int lBeforeReplace = s2.length();
                        s2 = s2.replace("  ", " ");
                        lengthChanged = (s2.length() != lBeforeReplace);
                    } while (lengthChanged);
                    reformattedMessage.append(s2).append("\n");
                }
            }
            CCLog.info(sourceCode);
            CCLog.info(textHighlightPos_problemStart, textHighlightPos_problemEnd);
            codeOfConcern = sourceCode.substring(textHighlightPos_problemStart, textHighlightPos_problemEnd);
            codeOfConcernLong = sourceCode.substring(textHighlightPos_lineStart, textHighlightPos_problemEnd);
            if (!codeOfConcern.isEmpty()) {
                reformattedMessage.append("Code of concern: \"").append(codeOfConcern).append('\"');
            }
            multiLineMessage = reformattedMessage.toString();
        }

        public String toStringForList() {

            if (compilerInfo == null) {
                return "No compiler!";
            } else {
                return compilerInfo.getCode();
            }
        }

        public String toStringForDebugging() {

            final StringBuilder ret = new StringBuilder();
            ret.append(compilerInfo.getSource().getName());
            ret.append("Type of problem: ").append(typeOfProblem_forDebugging).append("\n\n");
            ret.append("Message:\n").append(multiLineMessage).append("\n\n");

            ret.append(compilerInfo.getCode()).append("\n\n");

            ret.append("line number: ").append(lineNumber).append("\n");
            ret.append("column number: ").append(columnNumber).append("\n");

            ret.append("textHighlightPos_lineStart: ").append(textHighlightPos_lineStart).append("\n");
            ret.append("textHighlightPos_problemStart: ").append(textHighlightPos_problemStart).append("\n");
            ret.append("textHighlightPos_problemEnd: ").append(textHighlightPos_problemEnd).append("\n");

            return ret.toString();
        }

        @Override
        public String toString() {

            //            return compilerInfo.getMessage(Locale.US);
            return typeOfProblem + ": " + multiLineMessage + "\n";
        }
    }
}