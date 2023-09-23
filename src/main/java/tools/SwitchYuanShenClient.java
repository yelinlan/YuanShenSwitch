package tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.Setting;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

/**
 *@项目名称: YuanShenSwitch
 *@类名称: SwitchYuanShenClient
 *@类描述: 切换原神 天空岛与世界树
 *@创建人: yll
 *@创建时间: 2023/9/23 9:59
 **/
public class SwitchYuanShenClient {

	/**
	 * 初始化配置 安装目录 与 备份目录
	 */
	//安装路径
	// TODO: 2023/9/23 可以修改这里两个目录
	public static final String INSTALL_PATH = "C:\\Program Files\\Genshin Impact";
	//备份CONFIG
	public static final String BACK_UP_BASE = "D:\\YuanShen";

	/**
	 * GAME目录
	 */
	public static final String GAME_PATH = INSTALL_PATH + "\\Genshin Impact Game";
	//配置文件与插件
	public static final String GAME_CONFIG_PATH = GAME_PATH + "\\config.ini";
	// TODO: 2023/9/23 自己下载的插件放这里
	public static final String GAME_BILIBILI_PLUGIN = GAME_PATH + "\\YuanShen_Data\\Plugins\\PCGameSDK.dll";
	//备份
	public static final String GAME_BACK_UP_CONFIG_PATH_INIT = BACK_UP_BASE + "\\GAME\\config.ini.bak";
	//如果是B服会有这个插件。如果是官服就没有，需要手动放进来。
	public static final String GAME_BACK_UP_BILIBILI_PLUGIN_INIT = BACK_UP_BASE + "\\GAME\\PCGameSDK.dll.bak";

	/**
	 * 安装目录
	 */
	//配置文件与插件
	public static final String GLOBAL_CONFIG_PATH = INSTALL_PATH + "\\config.ini";
	//备份
	public static final String GLOBAL_BACK_UP_CONFIG_PATH_INIT = BACK_UP_BASE + "\\GLOBAL\\config.ini.bak";

	/**
	 * run
	 */
	public static void main(String[] args) throws InterruptedException {
		//初始化校验
		if (!validate()) {
			System.out.println("验证失败，即将退出！");
			Thread.sleep(5000);
			System.exit(0);
		}

		//初次备份
		firstBack();
		//start
		while (true) {
			//菜单
			System.out.println("=============切换菜单=========================");
			System.out.println("=             1.到B服【世界树】               =");
			System.out.println("=             2.到官服【天空岛】               =");
			System.out.println("=             3.备份的位置                    =");
			System.out.println("=             4.【B服的】PCGameSDK.dll放在哪   =");
			System.out.println("=             5.我要恢复原来的配置！            =");
			System.out.println("=             0.我要退出！                    =");
			System.out.println("=============================================");
			System.out.println("当前客户端为为【" + channnel() + "】");
			System.out.println(">>");
			Scanner scanner = new Scanner(System.in);
			int option = scanner.nextInt();
			switch (option) {
				case 1:
					//官服切B服
					switch2ShiJieShu();
					break;
				case 2:
					//B服切官服
					switch2TianKongDao();
					break;
				case 3:
					System.out.println("备份的config在这里：" + GAME_BACK_UP_CONFIG_PATH_INIT);
					break;
				case 4:
					System.out.println("PCGameSDK.dll在这里：" + GAME_BACK_UP_BILIBILI_PLUGIN_INIT);
					break;
				case 5:
					//还原GAME
					FileUtil.copy(GAME_BACK_UP_CONFIG_PATH_INIT, GAME_CONFIG_PATH, true);
					//还原INSTALL
					FileUtil.copy(GLOBAL_BACK_UP_CONFIG_PATH_INIT, GLOBAL_CONFIG_PATH, true);
					//还原插件
					if (isBiliBili()) {
						//如果是B服  还原 PCGameSDK.dll
						FileUtil.copy(GAME_BACK_UP_BILIBILI_PLUGIN_INIT, GAME_BILIBILI_PLUGIN, true);
					} else {
						//如果是官服  删除 PCGameSDK.dll
						FileUtil.del(GAME_BILIBILI_PLUGIN);
					}
					System.out.println("恢复完成！");
				case 0:
					System.exit(0);
					System.out.println("bye~");
					break;
			}
		}


	}

	/**
	 * 校验目录存在
	 */
	private static boolean validate() {
		//目录对不对
		if (!INSTALL_PATH.contains("Genshin Impact") || !FileUtil.exist(INSTALL_PATH)) {
			System.out.println("【警告】原神目录不正确！应该像这样：");
			System.out.println(INSTALL_PATH);
			return false;
		}

		//备份地址对不对
		if (!FileUtil.exist(BACK_UP_BASE)) {
			System.out.println("【警告】备份目录不存在！");
			System.out.println(BACK_UP_BASE);
			File file = new File(BACK_UP_BASE);
			System.out.println("【警告】开始创建备份目录！");
			if (file.mkdirs()) {
				System.out.println("【警告】备份目录为：");
				System.out.println(file.getAbsolutePath());
			}
		}
		return true;
	}

	/**
	 * 是bilibili？
	 */
	private static boolean isBiliBili() {
		return "bilibili".equals(clientName());
	}

	/**
	 * 客户端名字
	 */
	private static String clientName() {
		Setting setting = new Setting(GAME_CONFIG_PATH);
		return setting.getByGroup("cps", "General");
	}

	/**
	 * 客户端名字
	 */
	private static String channnel() {
		Setting setting = new Setting(GAME_CONFIG_PATH);
		String channel = setting.getByGroup("channel", "General");
		if ("1".equals(channel)) {
			return "官服";
		}
		if ("14".equals(channel)) {
			return "B服";
		}
		return "";
	}

	/**
	 * 初次备份
	 */
	private static void firstBack() {
		//备份GLOBLE_config.js
		FileUtil.copy(GLOBAL_CONFIG_PATH, GLOBAL_BACK_UP_CONFIG_PATH_INIT, false);
		//备份GAEM_config.js
		FileUtil.copy(GAME_CONFIG_PATH, GAME_BACK_UP_CONFIG_PATH_INIT, false);
		//如果是B服  备份BILIBILI PCGameSDK.dll
		if (isBiliBili()) {
			if (FileUtil.exist(GAME_BILIBILI_PLUGIN)) {
				FileUtil.copy(GAME_BILIBILI_PLUGIN, GAME_BACK_UP_BILIBILI_PLUGIN_INIT, false);
			} else {
				System.out.println("【警告】插件不存在！");
				System.out.println(GAME_BILIBILI_PLUGIN);
			}
		}

		//插件有没有
		if (!FileUtil.exist(GAME_BACK_UP_BILIBILI_PLUGIN_INIT)) {
			InputStream in = SwitchYuanShenClient.class.getClassLoader().getResourceAsStream("plugins/PCGameSDK.dll");
			FileUtil.writeFromStream(in, GAME_BACK_UP_BILIBILI_PLUGIN_INIT);
			System.out.println("【警告】没有找到【B服】PCGameSDK.dll插件");
			System.out.println("【警告】复制内部插件【可能过时！】。");
		}

	}

	/**
	 * 官服切B服
	 */
	private static void switch2ShiJieShu() {
		//复制插件
		FileUtil.copy(GAME_BACK_UP_BILIBILI_PLUGIN_INIT, GAME_BILIBILI_PLUGIN, true);
		//改变Game下的配置
		changeGameConfig("14", "bilibili");
		//改变安装目录下的配置
		changeInstallConfig("14", "bilibili");
	}

	/**
	 * 改变安装目录下的配置
	 */
	private static void changeInstallConfig(String channel, String cps) {
		Setting setting = new Setting(GLOBAL_CONFIG_PATH);
		setting.setByGroup("channel", "General", channel);
		setting.setByGroup("cps", "General", cps);
		setting.setByGroup("sub_channel", "General", channel);
		setting.store(GLOBAL_CONFIG_PATH);
	}

	/**
	 * B服切官服
	 */
	private static void switch2TianKongDao() {
		//移除BILIBILI插件
		FileUtil.del(GAME_BILIBILI_PLUGIN);
		//改变Game下的配置
		changeGameConfig("1", "mihoyo");
		//改变安装目录下的配置
		changeInstallConfig("1", "mihoyo");
	}

	/**
	 * 改变Game下的配置
	 */
	private static void changeGameConfig(String channel, String cps) {
		Setting setting = new Setting(GAME_CONFIG_PATH);
		setting.setByGroup("channel", "General", channel);
		setting.setByGroup("cps", "General", cps);
		setting.store(GAME_CONFIG_PATH);
	}

}