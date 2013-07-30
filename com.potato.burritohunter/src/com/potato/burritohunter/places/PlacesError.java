package com.potato.burritohunter.places;

import java.util.List;

public class PlacesError{
	private List<String> debug_info;
	private List<String> html_attributions;
	private List<String> results;
	private String status;
	
	public List<String> getDebug_info() {
		return debug_info;
	}
	public void setDebug_info(List<String> debug_info) {
		this.debug_info = debug_info;
	}
	public List<String> getHtml_attributions() {
		return html_attributions;
	}
	public void setHtml_attributions(List<String> html_attributions) {
		this.html_attributions = html_attributions;
	}
	public List<String> getResults() {
		return results;
	}
	public void setResults(List<String> results) {
		this.results = results;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}