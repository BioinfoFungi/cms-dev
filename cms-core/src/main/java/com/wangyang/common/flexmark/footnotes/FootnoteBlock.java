package com.wangyang.common.flexmark.footnotes;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.ParagraphItemContainer;
import com.wangyang.common.flexmark.footnotes.internal.FootnoteRepository;
import com.vladsch.flexmark.parser.ListOptions;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.ReferenceNode;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Footnote definition node containing text and other inline nodes nodes as children.
 */
public class FootnoteBlock extends Block implements ReferenceNode<FootnoteRepository, FootnoteBlock, Footnote>, ParagraphItemContainer {
    protected BasedSequence openingMarker = BasedSequence.NULL;
    protected BasedSequence text = BasedSequence.NULL;
    protected BasedSequence closingMarker = BasedSequence.NULL;
    protected BasedSequence footnote = BasedSequence.NULL;
    private String footnoteOrdinal;
    private String url;
    private int firstReferenceOffset = Integer.MAX_VALUE;
    private int footnoteReferences = 0;

    @Override
    public int compareTo(FootnoteBlock other) {
        return SequenceUtils.compare(text, other.text, true);
    }

    public int getFootnoteReferences() {
        return footnoteReferences;
    }

    public void setFootnoteReferences(int footnoteReferences) {
        this.footnoteReferences = footnoteReferences;
    }

    @Nullable
    @Override
    public Footnote getReferencingNode(@NotNull Node node) {
        return node instanceof Footnote ? (Footnote) node : null;
    }

    public int getFirstReferenceOffset() {
        return firstReferenceOffset;
    }

    public void setFirstReferenceOffset(int firstReferenceOffset) {
        this.firstReferenceOffset = firstReferenceOffset;
    }

    public void addFirstReferenceOffset(int firstReferenceOffset) {
        if (this.firstReferenceOffset < firstReferenceOffset) this.firstReferenceOffset = firstReferenceOffset;
    }

    public boolean isReferenced() {
        return this.firstReferenceOffset < Integer.MAX_VALUE;
    }

    public String getFootnoteOrdinal() {
        return footnoteOrdinal;
    }

    public void setFootnoteOrdinal(String footnoteOrdinal) {
        this.footnoteOrdinal = footnoteOrdinal;
    }

    @Override
    public void getAstExtra(@NotNull StringBuilder out) {
        out.append(" ordinal: ").append(footnoteOrdinal).append(" ");
        segmentSpan(out, openingMarker, "open");
        segmentSpan(out, text, "text");
        segmentSpan(out, closingMarker, "close");
        segmentSpan(out, footnote, "footnote");
    }

    @NotNull
    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { openingMarker, text, closingMarker, footnote };
    }

    public FootnoteBlock() {
    }

    public FootnoteBlock(BasedSequence chars) {
        super(chars);
    }

    public BasedSequence getOpeningMarker() {
        return openingMarker;
    }

    public void setOpeningMarker(BasedSequence openingMarker) {
        this.openingMarker = openingMarker;
    }

    public BasedSequence getText() {
        return text;
    }

    public void setText(BasedSequence text) {
        this.text = text;
    }

    public BasedSequence getClosingMarker() {
        return closingMarker;
    }

    public void setClosingMarker(BasedSequence closingMarker) {
        this.closingMarker = closingMarker;
    }

    public BasedSequence getFootnote() {
        return footnote;
    }

    public void setFootnote(BasedSequence footnote) {
        this.footnote = footnote;
    }

    @Override
    public boolean isItemParagraph(Paragraph node) {
        return node == getFirstChild();
    }

    @Override
    public boolean isParagraphWrappingDisabled(Paragraph node, ListOptions listOptions, DataHolder options) {
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean isParagraphInTightListItem(Paragraph node) {
        return false;
    }
}
