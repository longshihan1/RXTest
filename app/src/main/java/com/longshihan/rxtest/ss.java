/*
package com.longshihan.rxtest;


public class ss {
	NfcAdapter nfcAdapter;
	TextView promt;
	private PendingIntent pendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private boolean isFirst = true;

	private String roomNum;
	private String receId;

	public static final int GET_NFC_DATA = 3;
	private List<CardData> nfcList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_test);
		promt = (TextView) findViewById(R.id.promt);
		promt.setText("NFC");
		try {
			if (!com.huazhu.Card.NfcTools.isEnabled(this)) {
				promt.setText("请在系统设置中先启用NFC功能！");
				return;
			}
		} catch (NotSupportNfcException e) {
			promt.setText("设备不支持NFC!");
			return;
		}
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		// 注册回调函数
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass())
		.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY), 0);
		//动态注册NFC监听器
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		// ndef.addCategory("*
/*");
		mFilters = new IntentFilter[] { ndef };// 过滤器
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };// 允许扫描的标签类型

		roomNum = getIntent().getStringExtra("roomNumber");
		receId = getIntent().getStringExtra("RecieveOrderID");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter != null)
			//切换前台分派系统
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters,mTechLists);
		if (isFirst) {
			//提取NFC标签
			if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
				processIntent(getIntent());
			}
			isFirst = false;
		}
	}

	@Override
	public boolean onResponseSuccess(BasePaser parser, int requestId) {
		try {
			if (parser.getResultSuccess()) {
				switch (requestId) {
				case GET_NFC_DATA:
					DataNfcParser nfcParser = (DataNfcParser) parser;
					nfcList = nfcParser.getList();
					if (nfcList != null) {
						WriteData(LastTag, nfcList);
					}
					break;
				}
			}else{
				CommonFunction.ShowDialog(this,R.string.connect_error);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void WriteData(Tag tag, List<CardData> list) {
		try {
			// 进行写卡操作，只有在写卡的时候才可以进行该操作
			if (LastID == cardID && list != null) {
				List<com.huazhu.Card.CardData> writeNfcList = new ArrayList<com.huazhu.Card.CardData>();
				int len = list.size();
				// 类型转换
				for (int i = 0; i < len; i++) {
					com.huazhu.Card.CardData libCardData = new com.huazhu.Card.CardData();
					com.htinns.entity.CardData data = list.get(i);
					libCardData.Sector = data.Sector;
					libCardData.Block = data.Block;
					libCardData.Data = data.Data;
					libCardData.WritePassword = data.WritePassword;
					writeNfcList.add(libCardData);
				}
				// 若写卡失败，自己写
				boolean status = com.huazhu.Card.NfcTools.WriteCardData(tag,writeNfcList);
				if(status){
					Calling = false;
					CommonFunction.ShowDialog(this,R.string.nfc_make_room_card_success);
				}
				 Log.i("hb","nfc写卡状态："+status);
			} else {
				CommonFunction.ShowDialog(this,R.string.nfc_error);
			}
		} catch (Exception e) {
			CommonFunction.ShowDialog(this,R.string.nfc_write_error);
			e.printStackTrace();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			processIntent(intent);
		}
	}

	// 字符序列转换为16进制字符串
	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("0x");
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (nfcAdapter != null)
			//禁用前台分派系统
			nfcAdapter.disableForegroundDispatch(this);

	}

	private long LastID;
	private Tag LastTag = null;
	private boolean Calling = false;
	private long cardID = 0;

	private void processIntent(Intent intent) {
		// 取出封装在intent中的TAG
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		for (String tech : tagFromIntent.getTechList()) {
			//System.out.println(tech);
			Log.i("hb","nfc tag="+tech);
		}
		// 记录最后一次的tag对象
		LastTag = tagFromIntent;
		cardID = com.huazhu.Card.NfcTools.getCardId(tagFromIntent);
		// 从服务器端读取数据
		// 记录最后一次读取卡的ID
		if (!Calling) {
			LastID = cardID;
			Calling = true;
		}
		// -------------------------------

		// 以下数据只是用于测试使用，不作为实际开发的代码
		try{
			RequestInfo info = new RequestInfo(GET_NFC_DATA, HttpUtils.URL_NFC,
					new JSONObject().put("roomNumber", roomNum)
							.put("CardSNO", cardID)
							.put("RecieveOrderID", receId),
					new DataNfcParser(), this);
			info.isSign = true;
			HttpUtils.request(this, info);
		}catch(Exception e){
			e.printStackTrace();
		}


		boolean auth = false;
		// 读取TAG
		MifareClassic mfc = MifareClassic.get(tagFromIntent);
		try {
			String metaInfo = "";
			// Enable I/O operations to the tag from this TagTechnology object.
			mfc.connect();
			int type = mfc.getType();// 获取TAG的类型
			int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
			String typeS = "";
			switch (type) {
			case MifareClassic.TYPE_CLASSIC:
				typeS = "TYPE_CLASSIC";
				break;
			case MifareClassic.TYPE_PLUS:
				typeS = "TYPE_PLUS";
				break;
			case MifareClassic.TYPE_PRO:
				typeS = "TYPE_PRO";
				break;
			case MifareClassic.TYPE_UNKNOWN:
				typeS = "TYPE_UNKNOWN";
				break;
			}
			metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
					+ mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
					+ "B\n";
			for (int j = 0; j < sectorCount; j++) {
				// Authenticate a sector with key A.
				auth = mfc.authenticateSectorWithKeyA(j,
						MifareClassic.KEY_DEFAULT);
				int bCount;
				int bIndex;
				if (auth) {
					metaInfo += "Sector " + j + ":验证成功\n";
					// 读取扇区中的块
					bCount = mfc.getBlockCountInSector(j);
					bIndex = mfc.sectorToBlock(j);
					for (int i = 0; i < bCount; i++) {
						byte[] data = mfc.readBlock(bIndex);
						metaInfo += "Block " + bIndex + " : "
								+ bytesToHexString(data) + "\n";
						bIndex++;
					}
				} else {
					metaInfo += "Sector " + j + ":验证失败\n";
				}
			}
			promt.setText(metaInfo);
		} catch (TagLostException e) {
			Toast.makeText(this, "卡片离开", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void WriteData() throws Exception {
		Calling = false;
		// 进行写卡操作，只有在写卡的时候才可以进行该操作
		if (LastID == cardID)
			com.huazhu.Card.NfcTools.WriteCardData(LastTag, null);
		else
			throw new Exception("非同一张卡不允许进行此操作");
	}

}
*/
