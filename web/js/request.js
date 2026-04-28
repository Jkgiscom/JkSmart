/***
*			模拟request对象
*
*/
		var Request = new Object();
		Request = function(args){
				this.href = args["href"];
				this.query = [] ;
				this.query = this.href.indexOf("?")>-1?(this.href.split("?")[1].split("&")):[];
				this.params = [] ;
				//alert(this.query.length);
				for(var i=0; i<this.query.length; i++){
					var tmp = this.query[i].split("=");
					this.params[this.params.length] = {code:tmp[0], value:tmp[1]};
				}
		};
		
		Request.prototype.getParameter=function(k){
				//alert(k+","+this.params.length);
				var ret = null;
				for(var i=0; i<this.params.length; i++){
					//alert(this.params[i]["code"]+">>"+this.params[i]["value"]+(this.params[i]["code"]==k));
					if(this.params[i]["code"]==k){
						ret = this.params[i]["value"];
						break;
					}
				}
				return ret;
		};
		
		let request = new Request({href:window.location.href});