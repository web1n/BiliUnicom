package cx.yeah.bilibili.unicom;

import android.app.*;
import android.content.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;
import java.io.*;

/**
 * @author web1n
 * @description xposed
 * @email on@yeah.cx
 * @data 2018/1/20
 */

public class xposed implements IXposedHookLoadPackage 
{
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable
	{
		if (!lpparam.packageName.equals("tv.danmaku.bili") && !lpparam.packageName.equals("com.bilibili.app.blue") && !lpparam.packageName.equals("com.bilibili.app.in"))
            return;

		XposedBridge.log("BiliUnicom");
        XposedBridge.log("Loaded app: " + lpparam.packageName);

		XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
                    Context context=(Context)param.args[0];
					SharedPreferences.Editor edit = context.getSharedPreferences("bili_preference", 0).edit();
					edit.putLong("unicom.startup.last.check", System.currentTimeMillis());
					edit.commit();
					try
					{
						File file = new File("/data/data/" + context.getPackageName() + "/databases");
						File file3 = file;
						if (!file3.isDirectory())
						{
							file3.mkdir();
						}
						OutputStream outputStream = new FileOutputStream("/data/data/" + context.getPackageName() + "/databases/unicom.db");
						OutputStream outputStream2 = outputStream;
						Context ctxDealFile = context.createPackageContext("cx.yeah.bilibili.unicom", Context.CONTEXT_IGNORE_SECURITY);
						InputStream open = ctxDealFile.getAssets().open("unicom.db");
						byte[] bArr = new byte[1024];
						for (int read = open.read(bArr); read > 0; read = open.read(bArr))
						{
							outputStream2.write(bArr, 0, read);
						}
						outputStream2.flush();
						open.close();
						outputStream2.close();
					}
					catch (Exception e)
					{
						XposedBridge.log(e);
					}
                }
            });
	}
}
