using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace ScrapExampl
{
    abstract class AdListing
    {
        protected AdListing(string addr, string title, string text, string url, string price, string date)
        {
            this.Address = addr;
            this.Title = title;
            this.BriefDescription = text;
            this.Url = url;
            this.PostingDate = date;
            this.Price = price;
            this.ValidAddress = CanParseAddress();
        }

        protected abstract bool CanParseAddress();
        public bool ValidAddress { get; private set; }
        public string Address { get; private set; }
        public string Price { get; private set; }
        public string PostingDate { get; private set; }
        public string Title { get; private set; }
        public string BriefDescription { get; private set; }
        public string Url { get; private set; }
        public abstract string Source {get;}
    }

    class KAdListing : AdListing
    {
        public KAdListing(string addr, string title, string text, string url, string price, string date)
            : base(addr,title,text,url,price,date)
        {
        
        }

        public override string Source
        {
            get { return "Kijiji"; }
        }

        protected override bool CanParseAddress()
        {
            Match m1 = Regex.Match(this.Address, "[A-Z][0-9][A-Z] [0-9][A-Z][0-9]", RegexOptions.RightToLeft | RegexOptions.IgnoreCase);
            Match m2 = Regex.Match(this.Address, ".+,.+,.+,.+"); //street, city, province, country (NEEDS MORE TESTING on what Kijiji allows)
            return (m1.Success || m2.Success);
        }
    }

    class CAdListing : AdListing
    {
        //todo
        public CAdListing(string addr, string title, string text, string url, string price, string date)
            : base(addr,title,text,url,price,date)
        {
        
        }
        protected override bool CanParseAddress()
        {
            return false;
        }

        public override string Source
        {
            get { return "Craigslist"; }
        }
    }
}
