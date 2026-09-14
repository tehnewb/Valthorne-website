package com.example;

import java.util.Set;
import java.util.regex.Pattern;
import valthorne.graphics.Color;
import valthorne.ui.behavior.TextEditing;
import valthorne.ui.nodes.nano.*;

/** Syntax tokens are built once; Copy exports the original, unmodified snippet. */
final class CodeBlock extends NanoPanel {
    private static final Pattern TOKENS=Pattern.compile("//.*|\"(?:\\\\.|[^\"\\\\])*\"|'[^']*'|[A-Za-z_$][\\w$]*|\\d+(?:\\.\\d+)?[fLd]?|\\s+|.");
    private static final Set<String> KEYWORDS=Set.of("var","new","public","final","class","static","void","float","int","boolean","import","implements","true","false","return","if","else","for","while","null");
    CodeBlock(String code) {
        backgroundColor(new Color(0xFF0C0C11)).hoverBackgroundColor(new Color(0xFF0C0C11)).borderWidth(1).borderColor(Main.LINE).cornerRadius(12);
        getLayout().widthPercent(100).column().padding(18).gap(18).noShrink();
        var header=new NanoContainer(); header.getLayout().widthPercent(100).row().itemsCenter();
        header.add(new NanoLabel("JAVA  /  EXAMPLE").fontSize(11).color(Main.MUTED));
        var space=new NanoContainer(); space.getLayout().grow();header.add(space);
        var copy=new NanoButton("Copy code");
        copy.fontSize(12).paddingX(14).paddingY(9).cornerRadius(15).textColor(Main.WHITE).backgroundColor(Main.PANEL);
        copy.action(n->copy.text(TextEditing.copyText(code)?"Copied":"Copy unavailable"));
        header.add(copy);add(header);
        var lines=code.split("\n",-1);int max=0;for(var line:lines)max=Math.max(max,line.length());
        var viewport=new NanoScrollPanel().vertical(false).verticalBar(false).horizontal(true).horizontalBar(true);
        viewport.setStyle(NanoScrollPanel.BACKGROUND_COLOR_KEY,Main.CLEAR);viewport.setStyle(NanoScrollPanel.BORDER_WIDTH_KEY,0f);
        viewport.getLayout().widthPercent(100).height(lines.length*23+16).noShrink();
        var body=new NanoContainer();body.getLayout().column().width(Math.max(240,max*8.5f)).noShrink();
        for(var line:lines) {
            var row=new NanoContainer();row.getLayout().row().height(23).noShrink();
            var matcher=TOKENS.matcher(line);
            var run=new StringBuilder();int runColor=0xFFE2DFEB;
            while(matcher.find()) {
                String token=matcher.group();int rgb=0xFFE2DFEB;
                if(token.startsWith("//"))rgb=0xFF858293;
                else if(token.startsWith("\"")||token.startsWith("'"))rgb=0xFFA7D6AA;
                else if(KEYWORDS.contains(token))rgb=0xFFC5A6FF;
                else if(Character.isDigit(token.charAt(0)))rgb=0xFFF0C383;
                else if(Character.isUpperCase(token.charAt(0)))rgb=0xFF93CDE4;
                // Spaces can inherit the current run's color. Do not allocate a
                // Yoga node for every space or punctuation character in a snippet.
                if(token.isBlank())rgb=runColor;
                if(run.length()>0&&rgb!=runColor){addRun(row,run.toString(),runColor);run.setLength(0);}
                runColor=rgb;run.append(token);
            }
            if(run.length()>0)addRun(row,run.toString(),runColor);
            body.add(row);
        }
        viewport.setContent(body);add(viewport);
    }
    private static void addRun(NanoContainer row,String text,int color) {
        var span=new NanoLabel(text).fontName("code").fontSize(14).color(new Color(color)).selectable(true);
        span.getLayout().width(text.length()*8.4f).height(23).noShrink();row.add(span);
    }
}
