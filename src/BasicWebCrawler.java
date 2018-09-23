import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class BasicWebCrawler {
	
    private HashSet<String> links; // data structure to keep unique links
    private int limit; // # of links to crawl
    private ArrayList<Site> sites; // list of Site objects 
    private String scope; // restrict domain
    
    public BasicWebCrawler() {
        links = new HashSet<String>();
        limit = 0;
    	sites = new ArrayList<Site>();
    	scope = "";
    }

    public BasicWebCrawler(int limit){
        links = new HashSet<String>();
    	this.limit = limit;
    	sites = new ArrayList<Site>();
    	scope = "";
    }
    
    public BasicWebCrawler(int limit, String scope){
        links = new HashSet<String>();
    	this.limit = limit;
    	sites = new ArrayList<Site>();
    	this.scope = scope;
    }
    
    public void getPageLinks(String URL) throws InterruptedException {
        //4. Check if you have already crawled the URLs & check the scope
        if (!links.contains(URL) && URL.contains(scope)) {
            try {
                //4. (i) If not add it to the index
                if (links.size() < limit) {
                	links.add(URL);
                    System.out.println(URL);
                }
                else {
                	return;
                }
                
                //2. Fetch the HTML code
                Document document = Jsoup.connect(URL).get();
                System.out.println("made it");
                producePage(document);
                
                //To set a delay for accessing the same 
            	//Thread.sleep(5*1000); 

                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");
                //Find the number of outlinks in current URL for report
                int outlink = linksOnPage.size();

                //Find the number of images in current URL for report
                Elements imagesOnPage = document.select("img[src]");
                int image = imagesOnPage.size();
                
                //Get Response Status
                Response response = Jsoup.connect(URL).followRedirects(false).execute();
                int status = response.statusCode();
                
                //Create name for new file
                String directory = "repository/html_" + (links.size()) + ".html";
                
                Site newsite = new Site(URL, directory, status, outlink, image);
                sites.add(newsite);
                
                //5. For each extracted URL... go back to Step 4.
                for (Element page : linksOnPage) {
                		getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            } /*catch (InterruptedException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }*/
        }
    }

    //Produce html file of current URL
    public void producePage(Document Doc) throws IOException {
    	//Replace the destination & output file name
    	//File file = new File("/Users/wilsenkosasih/desktop/repository/html_"+ links.size() + ".html");
    	File file = new File("C:\\Users\\Vincent\\Desktop\\repository\\html_"+ links.size() + ".html");
    	
    	String html = Doc.html();
        
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(html);
		
		fileWriter.flush();
		fileWriter.close();
    }
    
    public void printToHTML(BasicWebCrawler bwc) {
    	
    	String html = "<div><h1>Welcome to our Web-Crawler Page!</h1><p>Results are shown below...";
    	
    	File f = new File("C:\\Users\\Vincent\\Desktop\\report.html");
    	
    	try{
            //1. clickable link to crawled URL.
            //2. link to downloaded page in repo folder.
            //3. HTTP status code
            //4. number of outlinks for crawled URL.
            //5. number of images
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(html + "<br><br>");
            
            html = "<table style=\"width:100%\"><tr><th style=\"; border: 1px solid black\">Clickable Link</th><th style=\"; border: 1px solid black\">Link to Repo Folder File</th>"
                    + "<th style=\"; border: 1px solid black\">HTTP status</th><th style=\"; border: 1px solid black\">No. outlinks</th><th style=\"; border: 1px solid black\">No. images</th></tr>";                    
            bw.write(html);
            html = "";
           //Access
        	for(int i = 0; i < bwc.sites.size(); i++) {
        		Site a = bwc.sites.get(i);
        		System.out.println();
        		html = "<tr>";
        		System.out.println(a.getUrl());
        		html += "<td style=\"; border: 1px solid black\">" + "<a href=\"" + a.getUrl() + "\">" + a.getUrl() + "</a></td>";
        		System.out.println(a.getDir());
        		html += "<td style=\"; border: 1px solid black\">" + "<a href=\"" + a.getDir() + "\">" + a.getDir() + "</a></td>";
        		System.out.println(a.getStatus());
        		html += "<td style=\"; border: 1px solid black\">" + a.getStatus() + "</td>";
        		System.out.println(a.getOutlink());
        		html += "<td style=\"; border: 1px solid black\">" + a.getOutlink() + "</td>";
        		System.out.println(a.getImages());
        		html += "<td style=\"; border: 1px solid black\">" + a.getImages() + "</td>";
        		html += "</tr>";
        		bw.write(html);
        		html = "";
        	}
            
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        //1. Pick a URL from the frontier
    	BasicWebCrawler BWC = new BasicWebCrawler(40, "www.google.com/about/");
    	BWC.getPageLinks("https://www.google.com/about/");
    	
    	BWC.printToHTML(BWC);
    }
}
