package com.khsoftsolutions.parenttracker.utilities;

/**
 * Created by myxroft on 01/10/2017.
 */

public class HTMLConstructor {

    private StringBuilder sb;

    public HTMLConstructor(){
        this.sb = new StringBuilder();
    }

    public void start(){
        this.sb.append("<html><body>");
    }

    public void addHeader(String head){
        this.sb.append("<h2>"+head+"<h2>");
    }

    public void addHorizontalRule(){
        this.sb.append("<hr />");
    }

    public void addTable(String[] colA, String[] colB){

        this.sb.append("<table border=1 width=100%>");

        for(int i=0;i<colA.length;i++){
            this.sb.append(setTR(setTD(colA[i]) + setTD(colB[i])));
        }
        this.sb.append("</tableA>");
    }

    public String setTD(String td){
        return "<td>" + td +"</td>";
    }

    public String setTR(String tr){
        return "<tr>" + tr + "</tr>";
    }

    public void end(){
        this.sb.append("</body></html>");
    }

    public String getHTMLString(){
        return this.sb.toString();
    }
}
