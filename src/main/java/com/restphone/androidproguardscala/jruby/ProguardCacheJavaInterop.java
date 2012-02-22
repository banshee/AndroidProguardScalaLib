package com.restphone.androidproguardscala.jruby;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.RubyClass;


public class ProguardCacheJavaInterop extends RubyObject  {
    private static final Ruby __ruby__ = Ruby.getGlobalRuntime();
    private static final RubyClass __metaclass__;

    static {
        String source = new StringBuilder("require 'java'\n" +
            "require 'proguard_cache.rb'\n" +
            "\n" +
            "java_package 'com.restphone.androidproguardscala.jruby'\n" +
            "\n" +
            "class ProguardCacheJavaInterop\n" +
            "  def initialize *args\n" +
            "    @ruby_object = ProguardCacheRuby.new\n" +
            "  end\n" +
            "  \n" +
            "  def build_proguard_dependency_files *args\n" +
            "    @ruby_object.build_proguard_dependency_files *args\n" +
            "  end\n" +
            "\n" +
            "  def run_proguard *args\n" +
            "    @ruby_object.run_proguard *args\n" +
            "  end\n" +
            "\n" +
            "  def install_proguard_output *args\n" +
            "    @ruby_object.install_proguard_output *args\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'void clean_cache(String cacheDir)'\n" +
            "\n" +
            "  def clean_cache cache_dir\n" +
            "    @ruby_object.clean_cache cache_dir\n" +
            "  end\n" +
            "end\n" +
            "").toString();
        __ruby__.executeScript(source, "src/main/jruby/proguard_cache_java_interop.rb");
        RubyClass metaclass = __ruby__.getClass("ProguardCacheJavaInterop");
        metaclass.setRubyStaticAllocator(ProguardCacheJavaInterop.class);
        if (metaclass == null) throw new NoClassDefFoundError("Could not load Ruby class: ProguardCacheJavaInterop");
        __metaclass__ = metaclass;
    }

    /**
     * Standard Ruby object constructor, for construction-from-Ruby purposes.
     * Generally not for user consumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    private ProguardCacheJavaInterop(Ruby ruby, RubyClass metaclass) {
        super(ruby, metaclass);
    }

    /**
     * A static method used by JRuby for allocating instances of this object
     * from Ruby. Generally not for user comsumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    public static IRubyObject __allocate__(Ruby ruby, RubyClass metaClass) {
        return new ProguardCacheJavaInterop(ruby, metaClass);
    }

    
    public  ProguardCacheJavaInterop(Object args) {
        this(__ruby__, __metaclass__);
        IRubyObject ruby_args = JavaUtil.convertJavaToRuby(__ruby__, args);
        RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "initialize", ruby_args);

    }

    
    public Object build_proguard_dependency_files(Object args) {
        IRubyObject ruby_args = JavaUtil.convertJavaToRuby(__ruby__, args);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "build_proguard_dependency_files", ruby_args);
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public Object run_proguard(Object args) {
        IRubyObject ruby_args = JavaUtil.convertJavaToRuby(__ruby__, args);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "run_proguard", ruby_args);
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public Object install_proguard_output(Object args) {
        IRubyObject ruby_args = JavaUtil.convertJavaToRuby(__ruby__, args);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "install_proguard_output", ruby_args);
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public void clean_cache(String cacheDir) {
        IRubyObject ruby_cacheDir = JavaUtil.convertJavaToRuby(__ruby__, cacheDir);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "clean_cache", ruby_cacheDir);
        return;

    }

}
