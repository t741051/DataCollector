package com.mitlab.zusliu.Update.List.View;

public class ListItem
{
    private String text1 = new String();
    private String text2 = new String();
    private String text3 = new String();
    private String text4 = new String();
    private String text5 = new String();
    private String text6 = new String();////////////////////////////////
    // using default parameter constructor
    public ListItem() { /***/ }

    // using custom parameter constructor
    public ListItem(String _t) { /***/ }

    public void setText1(String _text1) { this.text1 = _text1; }
    public void setText2(String _text2) { this.text2 = _text2; }
    public void setText3(String _text3) { this.text3 = _text3; }
    public void setText4(String _text4) { this.text4 = _text4; }
    public void setText5(String _text5) { this.text5 = _text5; }
    public void setText6(String _text6) { this.text6 = _text6; }///////////////////////
    public String getText1() { return this.text1; }
    public String getText2() { return this.text2; }
    public String getText3() { return this.text3; }
    public String getText4() { return this.text4; }
    public String getText5() { return this.text5; }
    public String getText6() { return this.text6; }//////////////////////////////////
}