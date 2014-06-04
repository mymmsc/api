package org.mymmsc.api.http.samples;

import java.util.HashMap;
import java.util.Map;

import org.mymmsc.api.io.HttpClient;
import org.mymmsc.api.io.HttpResult;

public class TestHttp401 {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String sUrl = "http://116.255.130.139:9103/users/me/apps?ids=0%3Acom.shuqi.controller%2C0%3AXXJUYLWERV.bookDiamondSutra%2C0%3Acom.oddsoft.buddhavoice%2C0%3Acom.iBookStar.activityon%2C0%3Acom.byread.reader%2C0%3Acom.ltzteamer.namespace.tuibeitu%2C0%3Acom.lingdong.client.android%2C0%3Acom.wandoujia.phoenix2%2C0%3Acom.example.android.apis%2C0%3Acom.sohu.sohuvideo%2C0%3Acom.wuba%2C0%3Acom.sina.weibo%2C0%3Acom.vic.manager%2C0%3Acom.kascend.video%2C0%3Acom.chaozh.iReaderFree%2C0%3Acom.ecapycsw.onetouchdrawing%2C0%3Acom.ijinshan.kbatterydoctor%2C0%3Acn.weizhang.android%2C0%3Acom.dianping.v1%2C0%3Acom.UCMobile%2C0%3Acn.etouch.ecalendar%2C0%3Acom.geolo.android%2C0%3Acom.kugou.android%2C0%3Acom.tx.buddha%2C0%3Acom.qihoo.browser%2C0%3Aeu.chainfire.supersu%2C0%3Acom.dolphin.browser.cn%2C0%3Acom.adobe.flashplayer%2C0%3Acom.cnblogs.android%2C0%3Auk.co.nickfines.RealCalc%2C0%3Acom.skvalex.callrecorder%2C0%3Acom.qihoo360.mobilesafe%2C0%3Acom.weizhang.droid%2C0%3Acom.aibang.abbus.bus%2C0%3Acom.nd.assistance%2C0%3Acom.fengxf.feng.activity%2C0%3Acn.zhangao.multithreaddownload%2C0%3Acom.zijunlin.Zxing.Demo%2C0%3Acom.qihoo.appstore%2C0%3Acom.tencent.mm%2C0%3Acom.tencent.mobileqq%2C0%3Aru.org.amip.ClockSync%2C0%3Acom.google.zxing.client.android%2C0%3Acom.baihe%2C0%3Acn.com.tiros.android.navidog%2C0%3Acom.jiasoft.swreader%2C0%3Acom.hx.view%2C0%3Acom.subatomicstudios%2C0%3Acom.google.android.voicesearch%2C0%3Aorg.loon.game%2C0%3Acn.btcall.ipcall%2C0%3Acom.antmobilesoft.book.fjjd%2C0%3Acom.baihe.face%2C0%3Acom.vodone.olympic%2C0%3Acom.yingyonghui.market%2C0%3Acom.jiayuan%2C0%3Acom.sohu.inputmethod.sogou%2C0%3Acom.sim.gerard.kickme%2C0%3Acn.com.fetion%2C0%3Ahh.hh%2C0%3Acom.google.android.apps.maps%2C0%3Acom.nfbazi.LiuyaoPaipan%2C&access_token=46e578f253588c57f8b304bc4789db3fcc13642f&device_token=d7a9614fe7ab0cb03b019ef57bd5c87dd57861b9&ver=1.0";
		//sUrl = "http://116.255.130.139:9103/users/me/apps";
		String params = "ids=0%3Acom.shuqi.controller%2C0%3AXXJUYLWERV.bookDiamondSutra%2C0%3Acom.oddsoft.buddhavoice%2C0%3Acom.iBookStar.activityon%2C0%3Acom.byread.reader%2C0%3Acom.ltzteamer.namespace.tuibeitu%2C0%3Acom.lingdong.client.android%2C0%3Acom.wandoujia.phoenix2%2C0%3Acom.example.android.apis%2C0%3Acom.sohu.sohuvideo%2C0%3Acom.wuba%2C0%3Acom.sina.weibo%2C0%3Acom.vic.manager%2C0%3Acom.kascend.video%2C0%3Acom.chaozh.iReaderFree%2C0%3Acom.ecapycsw.onetouchdrawing%2C0%3Acom.ijinshan.kbatterydoctor%2C0%3Acn.weizhang.android%2C0%3Acom.dianping.v1%2C0%3Acom.UCMobile%2C0%3Acn.etouch.ecalendar%2C0%3Acom.geolo.android%2C0%3Acom.kugou.android%2C0%3Acom.tx.buddha%2C0%3Acom.qihoo.browser%2C0%3Aeu.chainfire.supersu%2C0%3Acom.dolphin.browser.cn%2C0%3Acom.adobe.flashplayer%2C0%3Acom.cnblogs.android%2C0%3Auk.co.nickfines.RealCalc%2C0%3Acom.skvalex.callrecorder%2C0%3Acom.qihoo360.mobilesafe%2C0%3Acom.weizhang.droid%2C0%3Acom.aibang.abbus.bus%2C0%3Acom.nd.assistance%2C0%3Acom.fengxf.feng.activity%2C0%3Acn.zhangao.multithreaddownload%2C0%3Acom.zijunlin.Zxing.Demo%2C0%3Acom.qihoo.appstore%2C0%3Acom.tencent.mm%2C0%3Acom.tencent.mobileqq%2C0%3Aru.org.amip.ClockSync%2C0%3Acom.google.zxing.client.android%2C0%3Acom.baihe%2C0%3Acn.com.tiros.android.navidog%2C0%3Acom.jiasoft.swreader%2C0%3Acom.hx.view%2C0%3Acom.subatomicstudios%2C0%3Acom.google.android.voicesearch%2C0%3Aorg.loon.game%2C0%3Acn.btcall.ipcall%2C0%3Acom.antmobilesoft.book.fjjd%2C0%3Acom.baihe.face%2C0%3Acom.vodone.olympic%2C0%3Acom.yingyonghui.market%2C0%3Acom.jiayuan%2C0%3Acom.sohu.inputmethod.sogou%2C0%3Acom.sim.gerard.kickme%2C0%3Acn.com.fetion%2C0%3Ahh.hh%2C0%3Acom.google.android.apps.maps%2C0%3Acom.nfbazi.LiuyaoPaipan%2C&access_token=46e578f253588c57f8b304bc4789db3fcc13642f&device_token=d7a9614fe7ab0cb03b019ef57bd5c87dd57861b9&ver=1.0";
		//sUrl = Api.urlDecode(sUrl);
		System.out.println(sUrl.length() + ", " + sUrl);
		HttpClient hc = new HttpClient(sUrl, 30);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		HttpResult hRet = hc.post(null, "123");
		System.out.println("http-status=[" + hRet.getStatus() + "], body=["
				+ hRet.getBody() + "], message=" + hRet.getError());
	}

}
