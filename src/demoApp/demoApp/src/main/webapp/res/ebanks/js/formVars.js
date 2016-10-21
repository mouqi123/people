/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2016/1/18
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
(function( window ) {
    /*
     * demo:
     var formVars=new FormVars($modalAddForm.serializeArray());
     //formVars.sendNullVars=true;
     formVars.setItem('plate',formVars.getItem('plateProvince')+' '+formVars.getItem('plateNumber'));
     formVars.delItem('plateProvince');
     formVars.delItem('plateNumber');
     console.log(formVars.value());
     * */
    //任意拼接表单变量值的类，这样方便自动收集表单提交变量
    var FormVars=function(serializeArray,sendNullVars,mutilVarsConcatString){
        formVars=this;
        formVars._valueObj={};
        formVars.originObjArray=serializeArray;
        //默认不生成空值变量
        formVars.sendNullVars=sendNullVars||false;
        //默认多选框的变量值连接符为“,”
        formVars.mutilVarsConcatString=mutilVarsConcatString||',';
    };
    FormVars.prototype={
        constructor:FormVars,
        setItem:function(name,value){
            var needDefine=true;
            for(var i=0;i<formVars.originObjArray.length;i++){
                var currObj=formVars.originObjArray[i];
                if(currObj.name==name){
                    formVars.originObjArray[i].value=value;
                    needDefine=false;
                    break;
                }
            }
            if(needDefine){
                var newObj={};
                newObj.name=name;
                newObj.value=value;
                formVars.originObjArray.push(newObj);
            }
        },
        getItem:function(name){
            for(var i=0;i<formVars.originObjArray.length;i++){
                var currObj=formVars.originObjArray[i];
                if(currObj.name==name){
                    return currObj.value;
                }
            }
        },
        delItem:function(name){
            for(var i=0;i<formVars.originObjArray.length;i++){
                var currObj=formVars.originObjArray[i];
                if(currObj.name==name){
                    //formVars.originObjArray[i]=undefined;
                    formVars.originObjArray.splice(i,1);
                    return false;
                }
            }
        },
        //[{name:'a',value:1},{name:'a',value:2}]
        value:function(){
            formVars._valueObj={};
            for(var i=0;i<formVars.originObjArray.length;i++){
                var currObj=formVars.originObjArray[i];

                if(formVars.sendNullVars){
                    formVars._valueObj[currObj.name]=currObj.value;
                }else{
                    if(currObj.value){
                        if( formVars._valueObj[currObj.name] ){
                            var tmpExistValueObjName=formVars._valueObj[currObj.name];
                            formVars._valueObj[currObj.name]=tmpExistValueObjName+formVars.mutilVarsConcatString+currObj.value;
                        }else{
                            formVars._valueObj[currObj.name]=currObj.value;
                        }

                    }
                }
            }
            return formVars._valueObj;
        }
    };
    // EXPOSE
    if ( typeof define === "function" && define.amd ) {
        define(function() { return FormVars; });
    } else if ( typeof module !== "undefined" && module.exports ) {
        module.exports = FormVars;
    } else {
        window.FormVars = FormVars;
    }


})( window );