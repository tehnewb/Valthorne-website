package com.example;

import java.util.function.DoubleSupplier;
import valthorne.ui.NanoUtility;
import valthorne.ui.nodes.nano.NanoPanel;

/** Lightweight animated, engine-drawn previews; no borrowed project screenshots. */
final class ShowcaseArt extends NanoPanel {
    private final int kind;
    private final DoubleSupplier clock;
    ShowcaseArt(int kind, DoubleSupplier clock) {
        this.kind = kind; this.clock = clock;
        backgroundColor(Main.PANEL).hoverBackgroundColor(Main.PANEL).focusedBackgroundColor(Main.PANEL).pressedBackgroundColor(Main.PANEL);
        cornerRadius(12).borderWidth(1).borderColor(Main.LINE).hoverBorderColor(Main.LINE);
        setClickable(false);
    }
    @Override public void draw(long vg) {
        super.draw(vg);
        float x = getAbsoluteX(), y = getAbsoluteY(), w = getWidth(), h = getHeight(), t = (float)clock.getAsDouble();
        if (kind == 0) {
            for (int row=0;row<8;row++) for(int col=0;col<8;col++) {
                float xx=x+w*.5f+(col-row)*w*.042f, yy=y+h*.48f+(col+row-7)*h*.027f;
                int color = (row+col)%2==0?0xFF615570:0xFF26222F;
                NanoUtility.strokeLine(vg,xx,yy,xx+w*.042f,yy+h*.027f,color,2);
                NanoUtility.strokeLine(vg,xx+w*.042f,yy+h*.027f,xx,yy+h*.054f,color,2);
                NanoUtility.strokeLine(vg,xx,yy+h*.054f,xx-w*.042f,yy+h*.027f,color,2);
                NanoUtility.strokeLine(vg,xx-w*.042f,yy+h*.027f,xx,yy,color,2);
                if (row<2 || row>5) {
                    float lift=h*(.045f+.006f*(float)Math.sin(t+col));
                    NanoUtility.strokeLine(vg,xx,yy,xx,yy-lift,0xFFAA9BBC,5);
                    NanoUtility.strokeCircle(vg,xx,yy-lift,4,0xFFD2C4E7,3);
                }
            }
        } else if(kind == 1) {
            for(int i=0;i<36;i++) {
                float u=i/35f, yy=y+h*.5f+(float)Math.sin(u*6+t*.5f)*h*.19f;
                NanoUtility.strokeLine(vg,x+w*.1f+u*w*.8f,yy,x+w*.1f+u*w*.8f,y+h*.8f,0xFF4D286A,3);
                NanoUtility.strokeCircle(vg,x+w*.1f+u*w*.8f,yy,3,0xFFCAA8FF,2);
            }
        } else if(kind == 2) {
            for(int i=0;i<24;i++) {
                double a=i*2.39996+t*.04;
                float r=(float)Math.sqrt(i/24f)*Math.min(w,h)*.37f;
                float px=x+w*.5f+(float)Math.cos(a)*r, py=y+h*.5f+(float)Math.sin(a)*r;
                NanoUtility.strokeLine(vg,x+w*.5f,y+h*.5f,px,py,0xFF30273F,1);
                NanoUtility.strokeCircle(vg,px,py,3+i%3,0xFFB9A4DE,1.5f);
            }
        } else {
            for(int i=0;i<3;i++) {
                float xx=x+w*(.12f+i*.25f), yy=y+h*(.22f+i*.07f);
                NanoUtility.strokeLine(vg,xx,yy,xx+w*.27f,yy,0xFF393242,1);
                NanoUtility.strokeLine(vg,xx,yy,xx,yy+h*.47f,0xFF393242,1);
                NanoUtility.strokeLine(vg,xx,yy+h*.47f,xx+w*.27f,yy+h*.47f,0xFF393242,1);
                NanoUtility.strokeLine(vg,xx+w*.27f,yy,xx+w*.27f,yy+h*.47f,0xFF393242,1);
                for(int j=0;j<28;j++) {
                    float u=j/27f;
                    NanoUtility.strokeLine(vg,xx+w*.025f+u*w*.22f,yy+h*.23f+(float)Math.sin(u*15+t+i)*h*.04f,
                        xx+w*.025f+(u+.015f)*w*.22f,yy+h*.23f+(float)Math.sin((u+.015f)*15+t+i)*h*.04f,0xFF9673EE,3);
                }
            }
        }
    }
}
