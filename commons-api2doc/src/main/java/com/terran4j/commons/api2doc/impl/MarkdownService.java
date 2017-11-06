package com.terran4j.commons.api2doc.impl;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.KeepType;
import com.vladsch.flexmark.util.options.MutableDataSet;

@Service
public class MarkdownService {

	public String md2Html(String content) throws Exception {

		MutableDataSet options = new MutableDataSet();
		options.setFrom(ParserEmulationProfile.GITHUB_DOC);
		options.set(Parser.EXTENSIONS, //
				Arrays.asList(TablesExtension.create()));

		// References compatibility
		options.set(Parser.REFERENCES_KEEP, KeepType.LAST);

		// Set GFM table parsing options
		options.set(TablesExtension.COLUMN_SPANS, false) //
				.set(TablesExtension.MIN_HEADER_ROWS, 1) //
				.set(TablesExtension.MAX_HEADER_ROWS, 1) //
				.set(TablesExtension.APPEND_MISSING_COLUMNS, true) //
				.set(TablesExtension.DISCARD_EXTRA_COLUMNS, true) //
				.set(TablesExtension.WITH_CAPTION, false) //
				.set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);

		// Setup List Options for GitHub profile which is kramdown for documents
		options.setFrom(ParserEmulationProfile.GITHUB_DOC);

		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();

		// You can re-use parser and renderer instances
		Node document = parser.parse(content);
		String html = renderer.render(document);
		return html;
	}

}