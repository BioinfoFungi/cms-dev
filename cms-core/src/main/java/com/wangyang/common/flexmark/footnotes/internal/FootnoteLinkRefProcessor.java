package com.wangyang.common.flexmark.footnotes.internal;

import com.wangyang.common.flexmark.footnotes.Footnote;
import com.wangyang.common.flexmark.footnotes.FootnoteBlock;
import com.wangyang.common.flexmark.footnotes.FootnoteExtension;
import com.vladsch.flexmark.parser.LinkRefProcessor;
import com.vladsch.flexmark.parser.LinkRefProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;

public class FootnoteLinkRefProcessor implements LinkRefProcessor {
    static final boolean WANT_EXCLAMATION_PREFIX = false;
    static final int BRACKET_NESTING_LEVEL = 0;

    final private FootnoteRepository footnoteRepository;

    public FootnoteLinkRefProcessor(Document document) {
        this.footnoteRepository = FootnoteExtension.FOOTNOTES.get(document);
    }

    @Override
    public boolean getWantExclamationPrefix() {
        return WANT_EXCLAMATION_PREFIX;
    }

    @Override
    public int getBracketNestingLevel() {
        return BRACKET_NESTING_LEVEL;
    }

    @Override
    public boolean isMatch(@NotNull BasedSequence nodeChars) {
        return nodeChars.length() >= 3 && nodeChars.charAt(0) == '[' && nodeChars.charAt(1) == '^' && nodeChars.endCharAt(1) == ']';
    }

    @NotNull
    @Override
    public Node createNode(@NotNull BasedSequence nodeChars) {
        BasedSequence footnoteId = nodeChars.midSequence(2, -1).trim();
        String key = footnoteId.toString();
//        String key = footnoteRepository.getKey(footnoteId.toString());
        //        FootnoteBlock footnoteBlock = getFootnoteBlock(footnoteId.toString());
        FootnoteBlock footnoteBlock = footnoteId.length() > 0 ? footnoteRepository.get(key) : null;
//        FootnoteBlock footnoteBlock = new FootnoteBlock();
//        footnoteBlock.setText(BasedSequence.of("bbbbb"));
//        footnoteBlock.setFootnoteOrdinal("ssssssssssssss");
//        footnoteBlock.setOpeningMarker(openingMarker);
//        footnoteBlock.setText(text);
//        footnoteBlock.setClosingMarker(closingMarker);

        Footnote footnote = new Footnote(nodeChars.subSequence(0, 2), footnoteId, nodeChars.endSequence(1));
        if(footnoteBlock==null) {
            footnoteBlock = new FootnoteBlock();
        }
        footnote.setFootnoteBlock(footnoteBlock);
        footnoteRepository.addFootnoteReference(footnoteBlock,footnote);

//        if (footnoteBlock != null) {
//            footnoteRepository.addFootnoteReference(footnote);
//        }
        return footnote;
    }


    @NotNull
    @Override
    public BasedSequence adjustInlineText(@NotNull Document document, @NotNull Node node) {
        assert node instanceof Footnote;
        return ((Footnote) node).getText();
    }

    @Override
    public boolean allowDelimiters(@NotNull BasedSequence chars, @NotNull Document document, @NotNull Node node) {
        return true;
    }

    @Override
    public void updateNodeElements(@NotNull Document document, @NotNull Node node) {

    }

    public static class Factory implements LinkRefProcessorFactory {
        @NotNull
        @Override
        public LinkRefProcessor apply(@NotNull Document document) {
            return new FootnoteLinkRefProcessor(document);
        }

        @Override
        public boolean getWantExclamationPrefix(@NotNull DataHolder options) {
            return WANT_EXCLAMATION_PREFIX;
        }

        @Override
        public int getBracketNestingLevel(@NotNull DataHolder options) {
            return BRACKET_NESTING_LEVEL;
        }
    }
}
