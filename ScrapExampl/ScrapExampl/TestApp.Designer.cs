namespace ScrapExampl
{
    partial class TestApp
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.dataGridView1 = new System.Windows.Forms.DataGridView();
            this.txtUrl = new System.Windows.Forms.TextBox();
            this.btnLoad = new System.Windows.Forms.Button();
            this.lblMax = new System.Windows.Forms.Label();
            this.nudMax = new System.Windows.Forms.NumericUpDown();
            this.lblDelay = new System.Windows.Forms.Label();
            this.nudDelay = new System.Windows.Forms.NumericUpDown();
            this.txtStats = new System.Windows.Forms.TextBox();
            this.lblStats = new System.Windows.Forms.Label();
            this.gbSource = new System.Windows.Forms.GroupBox();
            this.rbCraigs = new System.Windows.Forms.RadioButton();
            this.rbKijiji = new System.Windows.Forms.RadioButton();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudMax)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudDelay)).BeginInit();
            this.gbSource.SuspendLayout();
            this.SuspendLayout();
            // 
            // dataGridView1
            // 
            this.dataGridView1.AllowUserToAddRows = false;
            this.dataGridView1.AllowUserToDeleteRows = false;
            this.dataGridView1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.dataGridView1.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.AllCells;
            this.dataGridView1.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataGridView1.Location = new System.Drawing.Point(23, 165);
            this.dataGridView1.Name = "dataGridView1";
            this.dataGridView1.ReadOnly = true;
            this.dataGridView1.Size = new System.Drawing.Size(901, 230);
            this.dataGridView1.TabIndex = 0;
            // 
            // txtUrl
            // 
            this.txtUrl.Location = new System.Drawing.Point(23, 14);
            this.txtUrl.Name = "txtUrl";
            this.txtUrl.Size = new System.Drawing.Size(505, 20);
            this.txtUrl.TabIndex = 1;
            // 
            // btnLoad
            // 
            this.btnLoad.Location = new System.Drawing.Point(537, 12);
            this.btnLoad.Name = "btnLoad";
            this.btnLoad.Size = new System.Drawing.Size(79, 23);
            this.btnLoad.TabIndex = 2;
            this.btnLoad.Text = "Load";
            this.btnLoad.UseVisualStyleBackColor = true;
            this.btnLoad.Click += new System.EventHandler(this.btnLoad_Click);
            // 
            // lblMax
            // 
            this.lblMax.AutoSize = true;
            this.lblMax.Location = new System.Drawing.Point(20, 48);
            this.lblMax.Name = "lblMax";
            this.lblMax.Size = new System.Drawing.Size(68, 13);
            this.lblMax.TabIndex = 3;
            this.lblMax.Text = "Max Results:";
            // 
            // nudMax
            // 
            this.nudMax.Increment = new decimal(new int[] {
            10,
            0,
            0,
            0});
            this.nudMax.Location = new System.Drawing.Point(104, 46);
            this.nudMax.Maximum = new decimal(new int[] {
            3000000,
            0,
            0,
            0});
            this.nudMax.Name = "nudMax";
            this.nudMax.Size = new System.Drawing.Size(120, 20);
            this.nudMax.TabIndex = 4;
            // 
            // lblDelay
            // 
            this.lblDelay.AutoSize = true;
            this.lblDelay.Location = new System.Drawing.Point(292, 48);
            this.lblDelay.Name = "lblDelay";
            this.lblDelay.Size = new System.Drawing.Size(161, 13);
            this.lblDelay.TabIndex = 5;
            this.lblDelay.Text = "Delay Between Page Loads (ms)";
            // 
            // nudDelay
            // 
            this.nudDelay.Increment = new decimal(new int[] {
            500,
            0,
            0,
            0});
            this.nudDelay.Location = new System.Drawing.Point(456, 46);
            this.nudDelay.Maximum = new decimal(new int[] {
            100000,
            0,
            0,
            0});
            this.nudDelay.Name = "nudDelay";
            this.nudDelay.Size = new System.Drawing.Size(120, 20);
            this.nudDelay.TabIndex = 6;
            // 
            // txtStats
            // 
            this.txtStats.Location = new System.Drawing.Point(81, 92);
            this.txtStats.Multiline = true;
            this.txtStats.Name = "txtStats";
            this.txtStats.ReadOnly = true;
            this.txtStats.Size = new System.Drawing.Size(713, 55);
            this.txtStats.TabIndex = 7;
            // 
            // lblStats
            // 
            this.lblStats.AutoSize = true;
            this.lblStats.Location = new System.Drawing.Point(20, 92);
            this.lblStats.Name = "lblStats";
            this.lblStats.Size = new System.Drawing.Size(31, 13);
            this.lblStats.TabIndex = 8;
            this.lblStats.Text = "Stats";
            // 
            // gbSource
            // 
            this.gbSource.Controls.Add(this.rbCraigs);
            this.gbSource.Controls.Add(this.rbKijiji);
            this.gbSource.Location = new System.Drawing.Point(632, 29);
            this.gbSource.Name = "gbSource";
            this.gbSource.Size = new System.Drawing.Size(212, 46);
            this.gbSource.TabIndex = 11;
            this.gbSource.TabStop = false;
            this.gbSource.Text = "Source";
            // 
            // rbCraigs
            // 
            this.rbCraigs.AutoSize = true;
            this.rbCraigs.Location = new System.Drawing.Point(121, 23);
            this.rbCraigs.Name = "rbCraigs";
            this.rbCraigs.Size = new System.Drawing.Size(66, 17);
            this.rbCraigs.TabIndex = 11;
            this.rbCraigs.TabStop = true;
            this.rbCraigs.Text = "Craigslist";
            this.rbCraigs.UseVisualStyleBackColor = true;
            // 
            // rbKijiji
            // 
            this.rbKijiji.AutoSize = true;
            this.rbKijiji.Location = new System.Drawing.Point(25, 23);
            this.rbKijiji.Name = "rbKijiji";
            this.rbKijiji.Size = new System.Drawing.Size(42, 17);
            this.rbKijiji.TabIndex = 10;
            this.rbKijiji.TabStop = true;
            this.rbKijiji.Text = "Kijiji";
            this.rbKijiji.UseVisualStyleBackColor = true;
            // 
            // TestApp
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.ClientSize = new System.Drawing.Size(936, 397);
            this.Controls.Add(this.gbSource);
            this.Controls.Add(this.lblStats);
            this.Controls.Add(this.txtStats);
            this.Controls.Add(this.nudDelay);
            this.Controls.Add(this.lblDelay);
            this.Controls.Add(this.nudMax);
            this.Controls.Add(this.lblMax);
            this.Controls.Add(this.btnLoad);
            this.Controls.Add(this.txtUrl);
            this.Controls.Add(this.dataGridView1);
            this.Name = "TestApp";
            this.Text = "TestApp";
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudMax)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudDelay)).EndInit();
            this.gbSource.ResumeLayout(false);
            this.gbSource.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.DataGridView dataGridView1;
        private System.Windows.Forms.TextBox txtUrl;
        private System.Windows.Forms.Button btnLoad;
        private System.Windows.Forms.Label lblMax;
        private System.Windows.Forms.NumericUpDown nudMax;
        private System.Windows.Forms.Label lblDelay;
        private System.Windows.Forms.NumericUpDown nudDelay;
        private System.Windows.Forms.TextBox txtStats;
        private System.Windows.Forms.Label lblStats;
        private System.Windows.Forms.GroupBox gbSource;
        private System.Windows.Forms.RadioButton rbCraigs;
        private System.Windows.Forms.RadioButton rbKijiji;
    }
}

