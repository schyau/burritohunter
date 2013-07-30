package com.potato.burritohunter.places;

import java.util.List;

public class PlacesSearchResult
{
	private List<String> debug_info;
	private List<String> html_attributions;
	private List<Stuffffz> results;
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
	public List<Stuffffz> getResults() {
		return results;
	}
	public void setResults(List<Stuffffz> results) {
		this.results = results;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}