using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;


namespace ScrapExampl
{
    public partial class TestApp : Form
    {

        BindingList<AdListing> listAds = new BindingList<AdListing>();
        KCrawler crawlK = new KCrawler();
        CCrawler crawlC = new CCrawler();

        public TestApp()
        {
            InitializeComponent();

            
            this.txtUrl.Text = "http://kitchener.kijiji.ca/f-buy-and-sell-furniture-beds-mattresses-W0QQCatIdZ246";
            this.rbKijiji.Checked = true;
            this.nudMax.Value = 20;            
        }

        private void btnLoad_Click(object sender, System.EventArgs e)
        {
            string url = this.txtUrl.Text;
            int max = (int)this.nudMax.Value;
            int delay = (int)this.nudDelay.Value;

            if (rbKijiji.Checked)
                this.listAds = crawlK.GetAds(url, max, delay);
            else
                MessageBox.Show("Not implemented");

            this.dataGridView1.DataSource = this.listAds;
            this.txtStats.Text = String.Format(" Retrieved ads: {0} \n Could Not Parse Address: {1} (no postal code and no street provided)", listAds.Count, listAds.Where(r => !r.ValidAddress).Count());

        }
    }
}
