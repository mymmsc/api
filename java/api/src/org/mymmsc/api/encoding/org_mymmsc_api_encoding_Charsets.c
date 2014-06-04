#include "org_mymmsc_api_encoding_Charsets.h"
#include <enca.h>
#include <stdio.h>
#include <string.h>

// char* To jstring  
jstring stringTojstring(JNIEnv *env, const char *pat)  
{   
    jclass strClass = (*env)->FindClass(env, "Ljava/lang/String;");   
    jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>", "([BLjava/lang/String;)V");   
    jbyteArray bytes = (*env)->NewByteArray(env, strlen(pat));   
    (*env)->SetByteArrayRegion(env, bytes, 0, strlen(pat), (jbyte*)pat);   
    jstring encoding = (*env)->NewStringUTF(env, "utf-8");   
    return (jstring)(*env)->NewObject(env, strClass, ctorID, bytes, encoding);   
}
jstring stringTojstring_OLD(JNIEnv *env, const char *pat)  
{   
    jclass strClass = (*env)->FindClass(env, "java/lang/String;");   
    jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>", "([Bjava/lang/String;)V");   
    jbyteArray bytes = (*env)->NewByteArray(env, strlen(pat));   
    (*env)->SetByteArrayRegion(env, bytes, 0, strlen(pat), (jbyte*)pat);   
    jstring encoding = (*env)->NewStringUTF(env, "utf-8");   
    return (jstring)(*env)->NewObject(env, strClass, ctorID, bytes, encoding);   
}

JNIEXPORT jstring JNICALL Java_org_mymmsc_api_encoding_Charsets_detect(JNIEnv *env, jclass jcls, jstring jstr)
{
	jstring sRet;
	const char *charsetName = NULL;
	EncaAnalyser encaAnalyser;
	EncaEncoding encaEncoding;
	unsigned char *s = NULL;
	size_t len = sizeof(jstr);
	s = (char *)malloc(sizeof(char) *len);
	strncpy(s, (char *)jstr, len);
	encaAnalyser = enca_analyser_alloc("zh");
    encaEncoding = enca_analyse(encaAnalyser, s, len);
    charsetName = enca_charset_name(encaEncoding.charset,
        ENCA_NAME_STYLE_ICONV);
	sRet = stringTojstring(env, charsetName);
    enca_analyser_free(encaAnalyser);
	free(s);
    return sRet;
}
