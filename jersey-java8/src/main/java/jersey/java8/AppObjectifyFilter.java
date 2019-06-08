package jersey.java8;

import com.googlecode.objectify.ObjectifyFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class AppObjectifyFilter extends ObjectifyFilter {}
