package com.golf.golf.config;

//import me.chanjar.weixin.common.util.http.DefaultApacheHttpClientBuilder;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 *
 * @author FirenzesEagle
 * @author BinaryWang
 */
@Configuration
public class MainConfig {

    @Value("#{wxProperties.appid}")
    private String appid;

    @Value("#{wxProperties.appsecret}")
    private String appsecret;

    @Value("#{wxProperties.token}")
    private String token;

    @Value("#{wxProperties.aeskey}")
    private String aesKey;

    @Value("#{wxProperties.partener_id}")
    private String partenerId;

    @Value("#{wxProperties.partener_key}")
    private String partenerKey;





	@Value("#{wxProperties.mini_appid}")
	private String miniAppid;

	@Value("#{wxProperties.mini_appsecret}")
	private String miniAppsecret;

	@Value("#{wxProperties.mini_token}")
	private String miniToken;

	@Value("#{wxProperties.mini_aeskey}")
	private String miniAesKey;


	/**
     * 如果出现 org.springframework.beans.BeanInstantiationException
     * https://github.com/Wechat-Group/weixin-java-tools-springmvc/issues/7
     * 请添加以下默认无参构造函数
     */
     protected MainConfig(){}
    
    /**
     * 为了生成自定义菜单使用的构造函数，其他情况Spring框架可以直接注入
     *
     * @param appid
     * @param appsecret
     * @param token
     * @param aesKey
     */
    protected MainConfig(String appid, String appsecret, String token, String aesKey, String miniAppid, String miniAppsecret, String miniToken) {
        this.appid = appid;
        this.appsecret = appsecret;
        this.token = token;
        this.aesKey = aesKey;
        this.miniAppid = miniAppid;
        this.miniAppsecret = miniAppsecret;
        this.miniToken = miniToken;
    }

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(this.appid);
        configStorage.setSecret(this.appsecret);
        configStorage.setToken(this.token);
        configStorage.setAesKey(this.aesKey);

//        DefaultApacheHttpClientBuilder clientBuilder = DefaultApacheHttpClientBuilder.get();
//        clientBuilder.setConnectionRequestTimeout(5000);//从连接池获取链接的超时时间(单位ms)
//        clientBuilder.setConnectionTimeout(10000);//建立链接的超时时间(单位ms)
//        clientBuilder.setSoTimeout(10000);//连接池socket超时时间(单位ms)
//        configStorage.setApacheHttpClientBuilder(clientBuilder); //设置自定义的ApacheHttpClientBuilder

        return configStorage;
    }


    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }


	@Bean
	public WxMaConfig wxConfigProvider() {
		WxMaInMemoryConfig configStorage = new WxMaInMemoryConfig();
		configStorage.setAppid(this.miniAppid);
		configStorage.setSecret(this.miniAppsecret);
		configStorage.setToken(this.miniToken);
		configStorage.setAesKey(this.miniAesKey);
		return configStorage;
	}

	@Bean
	public WxMaService wxMaService() {
		WxMaService wxMaService = new WxMaServiceImpl();
		wxMaService.setWxMaConfig(wxConfigProvider());
		return wxMaService;
	}

}
