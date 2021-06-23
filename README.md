#���
Afinal ��һ��android�� orm �� ioc ��ܡ����ҷ�װ��android�е�httpClient��ʹ����Ӽ����á�ʹ��finalBitmap�����迼��bitmap��android�м��ص�ʱ��oom������Ϳ��ٻ�����ʱ��ͼƬͼƬ��λ�����⡣

##ĿǰAfinal��Ҫ���Ĵ�ģ�飺

1��FinalDBģ�飺android�е�orm��ܣ�һ�д���Ϳ��Խ�����ɾ�Ĳ顣֧��һ�Զ࣬���һ�Ȳ�ѯ��

2��FinalActivityģ�飺android�е�ioc��ܣ���ȫע�ⷽʽ�Ϳ��Խ���UI�󶨺��¼��󶨡�����findViewById��setClickListener�ȡ�

3��FinalHttpģ�飺ͨ��httpclient���з�װhttp��������֧��ajax��ʽ���ء�

4��FinalBitmapģ�飺ͨ��FinalBitmap��imageview����bitmap��ʱ�����迼��bitmap���ع����г��ֵ�oom��android�������ٻ���ʱ����ֵ�ͼƬ��λ������FinalBitmap���������̼߳����߳������������С������·����������ʾ�����ȡ�FinalBitmap���ڴ����ʹ��lru�㷨��û��ʹ�������ã�android2.3�Ժ�google�Ѿ�������ʹ�������ã�android2.3��ǿ�л��������ú������ã�����鿴android�ٷ��ĵ��������õĹ���bitmap�ڴ档FinalBitmap�����Զ�������������ʽ��չ����Э���ͼƬ��ʾ������ftp�ȡ��Զ���bitmap��ʾ��������ʾ��ʱ�򲥷Ŷ����ȣ�Ĭ���ǽ��䶯����ʾ����

#ʹ��afinal���ٿ��������Ҫ������Ȩ�ޣ�

>uses-permission android:name="android.permission.INTERNET" 
>
>uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
>
>��һ���Ƿ�������
>
>�ڶ����Ƿ���sdcard
>
��������������������ͼƬ��ʱ����Ҫ������http��������ʱ����Ҫ������sdcard��ͼƬ�������Ҫ����


##FinalDBʹ�÷�����

>FinalDb db = FinalDb.create(this);
>                        
>User user = new User();
>
>user.setEmail("mail@tsz.net");
>
>user.setId(1);
>
>user.setName("michael yang");
>
>db.save(user);

##FinalActivityʹ�÷�����

>public class AfinalDemoActivity extends FinalActivity {
     //�������findViewById��setOnclickListener��
    @ViewInject(id=R.id.button,click="btnClick") Button button;
    @ViewInject(id=R.id.textView) TextView textView;
>       
>  public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
>  
    public void btnClick(View v){
        textView.setText("text set form button");
    }
}

##FinalHttpʹ�÷�����

>FinalHttp.ajax("http://www.yangfuhai.com/topic/7.html", new AjaxCallBack() {
>
	@Override
	public void callBack(AjaxStatus status) {
		textView.setText(status.getContentAsString());
	}
});

##FinalBitmap ʹ�÷��� (��������ͼƬ��һ�д��� fb.display(imageView,url) )��

>     private GridView gridView;
	private FinalBitmap fb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.images);
>		
>		  gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(mAdapter);
>		
>		  fb = new FinalBitmap(this).init();//�������init��ʼ��FinalBitmapģ��
		fb.configLoadingImage(R.drawable.downloading);
		//������Խ�������ʮ��������ã�Ҳ���Բ������ã�����֮��������init()����,����Ч
		//fb.configBitmapLoadThreadSize(int size)
		//fb.configBitmapMaxHeight(bitmapHeight)
	}


>///////////////////////////adapter getView////////////////////////////////////////////
>
> public View getView(int position, View convertView, ViewGroup parent) {
>
>	ImageView iv;
>
>	if(convertView == null){
>
	    convertView = View.inflate(BitmapCacheActivity.this,R.layout.image_item, null);
	    iv = (ImageView) convertView.findViewById(R.id.imageView);
	    iv.setScaleType(ScaleType.CENTER_CROP);
	    convertView.setTag(iv);
>
	}else{
	    iv = (ImageView) convertView.getTag();
	}
>	//bitmap���ؾ���һ�д��룬display�����������أ�����鿴Դ��
>
>	fb.display(iv,Images.imageUrls[position]);
>
>	return convertView;
>
>}
>

