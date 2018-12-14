Validator = {
	REQUIRE : /.+/,
	EMAIL : /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
	//PHONE : /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/,
	//Mobile : /^((\(\d{2,3}\))|(\d{3}\-))?13\d{9}$/,
	MOBILE : /(^\+86)?1\d{10}$/,
	URL : /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/,
	IDCARD : /^\d{15}(\d{2}[A-Za-z0-9])?$/,
	// 货币格式
	CURRENCY : /^\d+(\.\d+)?$/,
	// 数字
	NUMBER : /^\d+$/,
	// 仅作格式验证和基本有效日期验证，没做闰年的验证。yyyy-MM-dd
	// 现在二月默认是29天
	DATE : /(\d{3}[1-9]|\d{2}[1-9]\d{1}|\d{1}[1-9]\d{2}|[1-9]\d{3})-(((0[13578]|1[02])-(0[1-9]|[12]\d|3[01]))|((0[469]|11)-(0[1-9]|[12]\d|30))|(02-(0[1-9]|[1]\d|2\d)))/,
	//DATE : /\d{4}-\d{2}-\d{2}/,
	// 邮政编码
	ZIP : /^[1-9]\d{5}$/,
	QQ : /^[1-9]\d{4,8}$/,
	// 整数
	INTEGER : /^[-\+]?\d+$/,
	// 实数
	DOUBLE : /^[-\+]?\d+(\.\d+)?$/,
	ENGLISH : /^[A-Za-z]+$/,
	CHINESE : /^[\u0391-\uFFE5]+$/,
	IP : /^(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5])$/,
	
	// 可自定义正则表达式
	// required为false的时候，value没有值的时候不做正则判断
	exec : function(value, pattern, required) {
		//var req = required || true;
		if(required == null || required == undefined){
			required = true;
		}
		if(required){
			if(pattern.exec(value)){
				return true;
			}else{
				return false;
			}
		}else{
			if(value == null || value == undefined || value == ""){
				return true;
			}else{
				if(pattern.exec(value)){
					return true;
				}else{
					return false;
				}
			}
		}
	},
	// 是否是日期格式
	isDate : function(value, required){
		return this.exec(value, this.DATE, required)
	},
	// 是否是邮件
	isEmail : function(value, required){
		return this.exec(value, this.EMAIL, required)
	},
	// 是否是手机号
	isMobile : function(value, required){
		return this.exec(value, this.MOBILE, required)
	},
	// 是否是身份证
	isIdCard : function(value, required){
		return this.exec(value, this.IDCARD, required)
	},
	// 是否是数字
	isNumber : function(value, required){
		return this.exec(value, this.NUMBER, required)
	},
	// 是否是字母
	isEnglish : function(value, required){
		return this.exec(value, this.ENGLISH, required)
	}
	
}
