using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using HtmlAgilityPack;
using System.Net;
using System.ComponentModel;

namespace ScrapExampl
{

    abstract class Crawler
    {
        public abstract BindingList<AdListing> GetAds(string url, int maxResults, int delay);

        protected HtmlAgilityPack.HtmlDocument LoadPage(string url, int delay)
        {
            System.Threading.Thread.Sleep(delay);
            HtmlWeb hw = new HtmlWeb();            
            return hw.Load(url);
        }
    }

    class KCrawler : Crawler
    {
        public override BindingList<AdListing> GetAds(string url, int maxResults, int delay)
        {
            BindingList<AdListing> listAds = new BindingList<AdListing>();

            //Iterate through pages of results
            int iCount = 1;
            while (!String.IsNullOrEmpty(url) && iCount <= maxResults)
            {                
                HtmlAgilityPack.HtmlDocument doc = LoadPage(url, delay);
                
                //get all the ads from this page
                var adNodes = doc.DocumentNode.SelectNodes("//tr[ @class = 'resultsTableSB rrow'   and  @id [starts-with(., 'resultRow')] ]");
                
                if (adNodes != null)
                {
                    foreach (var adNode in adNodes)
                    {
                        //Get the ad details

                        //get url, title and brief description from "hgk"
                        var adDescNode = adNode.SelectSingleNode("td[@class = \" hgk\"]");
                        string adDesc = adDescNode.ChildNodes[3].InnerText;
                        adDesc = adDesc.Substring(1, adDesc.Length - 2);
                        var adHrefNode = adDescNode.SelectSingleNode("a");
                        string adUrl = adHrefNode.GetAttributeValue("href", String.Empty);
                        string adTitle = adHrefNode.InnerText;
                        
                        //Navigate to the ad to get additional fields such as location
                        HtmlAgilityPack.HtmlDocument doc2 = LoadPage(adUrl, delay);
                        var adFields = doc2.DocumentNode.SelectSingleNode("//table[@id = \"attributeTable\"]");
                        string addr = GetFieldValue(adFields, "Address\n");                        
                        addr = addr.Replace("View map", "");
                        string date = GetFieldValue(adFields, "Date Listed\n");
                        string price = GetFieldValue(adFields, "Price\n");
                        
                        listAds.Add(new KAdListing(addr, adTitle, adDesc, adUrl, price, date));

                        iCount++;
                        if (iCount > maxResults)
                            break;
                    }
                }
                url = GetNextPageUrl(doc);
            }

            return listAds;
        }

        private string GetFieldValue(HtmlNode nodeFields, string fieldName)
        {
            var tdField = nodeFields.SelectSingleNode(String.Format("//td[starts-with(.,'{0}')]", fieldName));           
            string val = "Unlisted / Not Found";
            if (tdField != null)
            {
                val = tdField.NextSibling.NextSibling.InnerText.Replace("\n", "");
                val = val.Trim();
                //need to trim whitespace at front and end
                //some fields start with \n, others end with \n, some have both and some neither
                //   -> also not sure if there are any fields that contain useful information over multiple lines i.e. \n in the middle
            }
            return val;
        }
        
        private string GetNextPageUrl(HtmlAgilityPack.HtmlDocument doc)
        {
            var a = doc.DocumentNode.SelectSingleNode("//a[@class = \"prevNextLink\" and starts-with(., 'Next')]");
            if (a == null)
            {
                return string.Empty;
            }
            else
            {
                return a.GetAttributeValue("href", String.Empty);
            }
        }
    }


    class CCrawler : Crawler
    {
        //todo
        public override BindingList<AdListing> GetAds(string url, int maxResults, int delay)
        {
            return null;
        }
    }
}
