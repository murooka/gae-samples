package jersey.java11;

import com.googlecode.objectify.ObjectifyFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class AppObjectifyFilter extends ObjectifyFilter {}
