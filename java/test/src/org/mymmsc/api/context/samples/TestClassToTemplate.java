/**
 * 
 */
package org.mymmsc.api.context.samples;

import java.io.IOException;
import java.util.Map;

import org.mymmsc.api.context.JsonAdapter;
import org.mymmsc.api.context.TemplateIterator;
import org.mymmsc.api.context.TemplateSyntaxException;
import org.mymmsc.api.context.Templator;

/**
 * @author WangFeng
 *
 */
public class TestClassToTemplate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "c:/temp/test.tpl";
		try {
			Templator tpl = new Templator(fileName);
			Map<String, String> map = tpl.getVariables();
			if (map != null) {
				System.out.println(map.size());
			}
			String string = "{\"order\":{\"id\":123,\"name\":\"bichao\"},\"status\":0,\"message\":\"success\",\"bills\":[{\"amount\":\"11\",\"test\":{\"amount\":\"12\",\"billId\":\"billid2\"},\"billId\":\"billid1\"},{\"amount\":\"13\",\"billId\":\"billid3\"}]}";
			//String string = "{\"bills\":[{\"amount\":\"11\",\"test\":{\"amount\":\"12\",\"billId\":\"billid2\"},\"billId\":\"billid1\"},{\"amount\":\"13\",\"billId\":\"billid3\"}]}";
			System.out.println("第1次 ===>");
			System.out.println("       原json串: " + string);
			// 解析JSON串, 有可能因解析失败返回null
			JsonAdapter parser = JsonAdapter.parse(string);
			if (parser != null) {
				TObject obj = parser.get(TObject.class);
				TemplateIterator ti = new TemplateIterator(tpl);
				ti.transit(null, obj);
				System.out.println(tpl.generateOutput());
				
				Test test = new Test();
				test.setAmount("1");
				test.setBillId("2");
				System.out.println("输出bean的JSON串: "  + JsonAdapter.get(test, false));
			}
		} catch (TemplateSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
