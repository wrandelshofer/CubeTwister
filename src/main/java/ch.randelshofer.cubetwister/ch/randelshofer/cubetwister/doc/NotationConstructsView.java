/* @(#)NotationConstructsView.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.Fonts;
import ch.randelshofer.gui.ScrollablePanel;
import ch.randelshofer.gui.event.DocumentAdapter;
import ch.randelshofer.gui.event.GenericListener;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The NotationConstructsView is an editor for the expression tokens of a notation.
 *
 * @author Werner Randelshofer
 */
public class NotationConstructsView extends ScrollablePanel
        implements EntityView, PropertyChangeListener {
    private final static long serialVersionUID = 1L;
    private NotationModel model;
    
    private JCheckBox[]
            checkBoxes;
    
    private JComponent[]
            columnHeaders,
            permutationFields,
            groupingFields,
            commutationFields,
            conjugationFields,
            rotationFields,
            repetitionFields,
            reflectionFields,
            inversionFields,
            delimiterFields,
            commentFields;
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;

    @Nonnull
    private ArrayList<TokenAdapter> tokenFields = new ArrayList<TokenAdapter>();
    @Nonnull
    private ArrayList<SyntaxAdapter> syntaxFields = new ArrayList<SyntaxAdapter>();
    
    private class TokenAdapter
            extends DocumentAdapter {
        private Symbol symbol;
        
        public TokenAdapter(Symbol symbol, @Nonnull JTextField textField) {
            super(textField);
            this.symbol = symbol;
            tokenFields.add(this);
        }

        public void updateFromModel() {
            if (NotationConstructsView.this.model != null) {
                setText(NotationConstructsView.this.model.getAllTokens(symbol));
            }
        }
        
        @Override
        public void documentChanged(@Nonnull DocumentEvent evt) {
            if (NotationConstructsView.this.model != null) {
                NotationConstructsView.this.model.setToken(symbol, Normalizer.normalize(getText(evt), Normalizer.Form.NFC));
            }
        }
    }
    private class SyntaxAdapter implements ActionListener {
        private Symbol symbol;
        private Syntax syntax;
        private JRadioButton radio;
        
        public SyntaxAdapter(Symbol symbol, Syntax syntax, @Nonnull JRadioButton radio) {
            this.symbol = symbol;
            this.syntax = syntax;
            this.radio = radio;
            radio.addActionListener(this);
            syntaxFields.add(this);
        }

        public void updateFromModel() {
            if (NotationConstructsView.this.model != null) {
                radio.setSelected(
                        NotationConstructsView.this.model.getSyntax(symbol) == syntax
                        );
            }
        }
        
        public void actionPerformed(ActionEvent e) {
            if (radio.isSelected() &&
                    NotationConstructsView.this.model != null) {
                NotationConstructsView.this.model.setSyntax(symbol, syntax);
            }
        }
    }
    
    /** Creates new form CubeScriptView */
    public NotationConstructsView() {
        init();
    }
    private void init() {
        labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.cubetwister.Labels"));
        
        initComponents();

        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(24);


        Font f = Fonts.getDialogFont();
        Component[] c = tokensPanel.getComponents();
        for (int i=0; i < c.length; i++) {
            c[i].setFont(f);
        }
        
        pCheckBox.putClientProperty("symbol", Symbol.PERMUTATION);
        groupingCheckBox.putClientProperty("symbol", Symbol.GROUPING);
        inversionCheckBox.putClientProperty("symbol", Symbol.INVERSION);
        reflectionCheckBox.putClientProperty("symbol", Symbol.REFLECTION);
        repetitionCheckBox.putClientProperty("symbol", Symbol.REPETITION);
        conjugationCheckBox.putClientProperty("symbol", Symbol.CONJUGATION);
        commutationCheckBox.putClientProperty("symbol", Symbol.COMMUTATION);
        rotationCheckBox.putClientProperty("symbol", Symbol.ROTATION);
        delimitersCheckBox.putClientProperty("symbol", Symbol.DELIMITER);
        commentsCheckBox.putClientProperty("symbol", Symbol.COMMENT);
        
        columnHeaders = new JComponent[] {
            rightLabel, upLabel, frontLabel, leftLabel, downLabel, backLabel
        };
        
        checkBoxes = new JCheckBox[] {
            pCheckBox,
            groupingCheckBox,
            inversionCheckBox,
            reflectionCheckBox,
            repetitionCheckBox,
            conjugationCheckBox,
            commutationCheckBox,
            rotationCheckBox,
            delimitersCheckBox,
            commentsCheckBox,
            
        };
        ItemListener symbolSupportHandler = (ItemListener) (GenericListener.create(
                ItemListener.class,
                "itemStateChanged",
                this,
                "symbolSupportChanged")
                );
        f = Fonts.getEmphasizedDialogFont();
        for (JCheckBox cb : checkBoxes) {
            cb.addItemListener(symbolSupportHandler);
            cb.setFont(f);
        }
        
        permutationFields = new JComponent[] {
            permFacesLabel, permRField, permUField, permFField, permLField, permDField, permBField,
            signsLabel, plusLabel, permPlusField, plusPlusLabel, permPlusPlusField, minusLabel, permMinusField,
            permCycleLabel,
            permBeginLabel,
            permBeginField,
            permEndLabel,
            permEndField,
            permDelimLabel,
            permDelimField,
            permSyntaxLabel,
            permPrecircumfixRadioButton,
            permPostcircumfixRadioButton,
            permPrefixRadioButton,
            permSuffixRadioButton,
        };
        groupingFields = new JComponent[] {
            bracketsLabel,
            groupingBeginLabel, groupingEndLabel,
            groupingBeginField, groupingEndField,
        };
        repetitionFields = new JComponent[] {
            repetitionLabel, 
            repetitionBeginLabel,
            repetitionBeginField,
            repetitionEndLabel,
            repetitionEndField,
            repetitionSyntaxLabel,
            repetitionPrefixRadioButton,
            repetitionSuffixRadioButton
        };
        inversionFields = new JComponent[] {
            invertorLabel,
            invertorField,
            inversionLabel,
            inversionBeginLabel,
            inversionBeginField,
            inversionEndLabel,
            inversionEndField,
            inversionSyntaxLabel,
            inversionPrefixRadioButton,
            inversionSuffixRadioButton,
            inversionCircumfixRadioButton
        };
        reflectionFields = new JComponent[] {
            reflectionLabel,
            reflectorLabel,
            reflectorField,
            reflectionBeginLabel,
            reflectionBeginField,
            reflectionEndLabel,
            reflectionEndField,
            reflectionSyntaxLabel,
            reflectionPrefixRadioButton,
            reflectionSuffixRadioButton,
            reflectionCircumfixRadioButton
        };
        conjugationFields = new JComponent[] {
            conjugationLabel,
            conjugationBeginLabel,
            conjugationBeginField,
            conjugationEndLabel,
            conjugationEndField,
            conjugationDelimLabel,
            conjugationDelimField,
            conjugationSyntaxLabel,
            conjugationPrefixRadioButton,
            conjugationSuffixRadioButton,
            conjugationPrecircumfixRadioButton,
            conjugationInfixRadioButton,
        };
        commutationFields = new JComponent[] {
            commutationLabel,
            commutationBeginLabel,
            commutationBeginField,
            commutationEndLabel,
            commutationEndField,
            commutationDelimLabel,
            commutationDelimField,
            commutationSyntaxLabel,
            commutationPrefixRadioButton,
            commutationSuffixRadioButton,
            commutationPrecircumfixRadioButton,
            commutationInfixRadioButton,
        };
        rotationFields = new JComponent[] {
            rotationLabel,
            rotationBeginLabel,
            rotationBeginField,
            rotationEndLabel,
            rotationEndField,
            rotationDelimLabel,
            rotationDelimField,
            rotationSyntaxLabel,
            rotationPrefixRadioButton,
            rotationSuffixRadioButton,
            rotationPrecircumfixRadioButton,
            rotationInfixRadioButton,
        };
        delimiterFields = new JComponent[] {
            delimiterLabel,
            statementDelimField,
            nopLabel,
            nopField,
        };
        commentFields = new JComponent[] {
            commentMultiLineBeginLabel,
            commentMultiLineBeginField,
            commentMultiLineEndLabel,
            commentMultiLineEndField,
            commentSingleLineLabel,
            commentSingleLineBeginField
        };
        
        new SyntaxAdapter(Symbol.PERMUTATION, Syntax.PREFIX, permPrefixRadioButton);
        new SyntaxAdapter(Symbol.PERMUTATION, Syntax.SUFFIX, permSuffixRadioButton);
        new SyntaxAdapter(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX, permPrecircumfixRadioButton);
        new SyntaxAdapter(Symbol.PERMUTATION, Syntax.POSTCIRCUMFIX, permPostcircumfixRadioButton);
        
        new SyntaxAdapter(Symbol.REPETITION, Syntax.PREFIX, repetitionPrefixRadioButton);
        new SyntaxAdapter(Symbol.REPETITION, Syntax.SUFFIX, repetitionSuffixRadioButton);

        new SyntaxAdapter(Symbol.INVERSION, Syntax.PREFIX, inversionPrefixRadioButton);
        new SyntaxAdapter(Symbol.INVERSION, Syntax.SUFFIX, inversionSuffixRadioButton);
        new SyntaxAdapter(Symbol.INVERSION, Syntax.CIRCUMFIX, inversionCircumfixRadioButton);

        new SyntaxAdapter(Symbol.REFLECTION, Syntax.PREFIX, reflectionPrefixRadioButton);
        new SyntaxAdapter(Symbol.REFLECTION, Syntax.SUFFIX, reflectionSuffixRadioButton);
        new SyntaxAdapter(Symbol.REFLECTION, Syntax.CIRCUMFIX, reflectionCircumfixRadioButton);

        new SyntaxAdapter(Symbol.COMMUTATION, Syntax.PREFIX, commutationPrefixRadioButton);
        new SyntaxAdapter(Symbol.COMMUTATION, Syntax.SUFFIX, commutationSuffixRadioButton);
        new SyntaxAdapter(Symbol.COMMUTATION, Syntax.PRECIRCUMFIX, commutationPrecircumfixRadioButton);
        new SyntaxAdapter(Symbol.COMMUTATION, Syntax.PREINFIX, commutationInfixRadioButton);

        new SyntaxAdapter(Symbol.CONJUGATION, Syntax.PREFIX, conjugationPrefixRadioButton);
        new SyntaxAdapter(Symbol.CONJUGATION, Syntax.SUFFIX, conjugationSuffixRadioButton);
        new SyntaxAdapter(Symbol.CONJUGATION, Syntax.PRECIRCUMFIX, conjugationPrecircumfixRadioButton);
        new SyntaxAdapter(Symbol.CONJUGATION, Syntax.POSTINFIX, conjugationInfixRadioButton);

        new SyntaxAdapter(Symbol.ROTATION, Syntax.PREFIX, rotationPrefixRadioButton);
        new SyntaxAdapter(Symbol.ROTATION, Syntax.SUFFIX, rotationSuffixRadioButton);
        new SyntaxAdapter(Symbol.ROTATION, Syntax.PRECIRCUMFIX, rotationPrecircumfixRadioButton);
        new SyntaxAdapter(Symbol.ROTATION, Syntax.PREINFIX, rotationInfixRadioButton);

        new TokenAdapter(Symbol.FACE_R, permRField);
        new TokenAdapter(Symbol.FACE_U, permUField);
        new TokenAdapter(Symbol.FACE_F, permFField);
        new TokenAdapter(Symbol.FACE_L, permLField);
        new TokenAdapter(Symbol.FACE_D, permDField);
        new TokenAdapter(Symbol.FACE_B, permBField);
        new TokenAdapter(Symbol.PERMUTATION_PLUS, permPlusField);
        new TokenAdapter(Symbol.PERMUTATION_MINUS, permMinusField);
        new TokenAdapter(Symbol.PERMUTATION_PLUSPLUS, permPlusPlusField);
        new TokenAdapter(Symbol.PERMUTATION_BEGIN, permBeginField);
        new TokenAdapter(Symbol.PERMUTATION_END, permEndField);
        new TokenAdapter(Symbol.PERMUTATION_DELIMITER, permDelimField);
        new TokenAdapter(Symbol.DELIMITER, statementDelimField);
        new TokenAdapter(Symbol.INVERSION_BEGIN, inversionBeginField);
        new TokenAdapter(Symbol.INVERSION_END, inversionEndField);
        //new TokenAdapter(Symbol.INVERSION_DELIMITER, inversionDelimField);
        new TokenAdapter(Symbol.INVERSION_OPERATOR, invertorField);
        new TokenAdapter(Symbol.REFLECTION_BEGIN, reflectionBeginField);
        new TokenAdapter(Symbol.REFLECTION_END, reflectionEndField);
        //new TokenAdapter(Symbol.REFLECTION_DELIMITER, reflectionDelimField);
        new TokenAdapter(Symbol.REFLECTION_OPERATOR, reflectorField);
        new TokenAdapter(Symbol.GROUPING_BEGIN, groupingBeginField);
        new TokenAdapter(Symbol.GROUPING_END, groupingEndField);
        new TokenAdapter(Symbol.REPETITION_BEGIN, repetitionBeginField);
        new TokenAdapter(Symbol.REPETITION_END, repetitionEndField);
        //new TokenAdapter(Symbol.REPETITION_DELIMITER, repetitionDelimField);
        new TokenAdapter(Symbol.COMMUTATION_BEGIN, commutationBeginField);
        new TokenAdapter(Symbol.COMMUTATION_END, commutationEndField);
        new TokenAdapter(Symbol.COMMUTATION_DELIMITER, commutationDelimField);
        new TokenAdapter(Symbol.CONJUGATION_BEGIN, conjugationBeginField);
        new TokenAdapter(Symbol.CONJUGATION_END, conjugationEndField);
        new TokenAdapter(Symbol.CONJUGATION_DELIMITER, conjugationDelimField);
        new TokenAdapter(Symbol.ROTATION_BEGIN, rotationBeginField);
        new TokenAdapter(Symbol.ROTATION_END, rotationEndField);
        new TokenAdapter(Symbol.ROTATION_OPERATOR, rotationDelimField);
        //new TokenAdapter(Symbol.MACRO, macroField);
        
        
        new TokenAdapter(Symbol.DELIMITER, statementDelimField);
        new TokenAdapter(Symbol.NOP, nopField);
        
        new TokenAdapter(Symbol.MULTILINE_COMMENT_BEGIN, commentMultiLineBeginField);
        new TokenAdapter(Symbol.MULTILINE_COMMENT_END, commentMultiLineEndField);
        new TokenAdapter(Symbol.SINGLELINE_COMMENT_BEGIN, commentSingleLineBeginField);
        
        
        setModel(null);
    }
    
    public void setModel(NotationModel s) {
        if (model != null) {
            model.removePropertyChangeListener(this);
        }
        model = s;
        if (model != null) {
            model.addPropertyChangeListener(this);
        }
        updateLanguageFeatures();
        updateFields();
        updateSyntax();
        updateEnabled();
    }
    
    public void symbolSupportChanged(@Nonnull ItemEvent evt) {
        if (model != null) {
            JCheckBox cb = (JCheckBox) evt.getSource();
            model.setSupported((Symbol) cb.getClientProperty("symbol"), cb.isSelected());
        }
    }

    private void updateFields() {
        if (model != null) {
            for (TokenAdapter ta : tokenFields) {
                ta.updateFromModel();
            }
        }
    }
    private void updateSyntax() {
        if (model != null) {
            for (SyntaxAdapter sa : syntaxFields) {
                sa.updateFromModel();
            }
        }
    }
    
    private void updateLanguageFeatures() {
        NotationModel m = (model == null) ? new NotationModel() : model;
        
        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(m.isSupported((Symbol) cb.getClientProperty("symbol")));
        }
        
        setVisible(columnHeaders, m.isSupported(Symbol.PERMUTATION));
        setVisible(permutationFields, m.isSupported(Symbol.PERMUTATION));
        setVisible(groupingFields, m.isSupported(Symbol.GROUPING));
        setVisible(inversionFields, m.isSupported(Symbol.INVERSION));
        setVisible(conjugationFields, m.isSupported(Symbol.CONJUGATION));
        setVisible(commutationFields, m.isSupported(Symbol.COMMUTATION));
        setVisible(rotationFields, m.isSupported(Symbol.ROTATION));
        setVisible(repetitionFields, m.isSupported(Symbol.REPETITION));
        setVisible(reflectionFields, m.isSupported(Symbol.REFLECTION));
        setVisible(delimiterFields, m.isSupported(Symbol.DELIMITER));
        setVisible(commentFields, m.isSupported(Symbol.COMMENT));
        validate();
    }

    private void setVisible(@Nonnull JComponent[] c, boolean b) {
        for (int i = 0; i < c.length; i++) {
            c[i].setVisible(b);
        }
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        updateEnabled();
    }
    
    public void updateEnabled() {
        boolean b = model != null && isEnabled();
        java.awt.Component[] c = tokensPanel.getComponents();
        for (int i=0; i < c.length; i++) {
            c[i].setEnabled(b);
        }
        
    }
    
    @Override
    public void propertyChange(@Nonnull PropertyChangeEvent evt) {
        if (evt.getSource() == model) {
            String n = evt.getPropertyName();
            if (n == NotationModel.PROP_STATEMENT_TOKEN) {
                updateFields();
            } else if (n == NotationModel.PROP_SYNTAX) {
                updateSyntax();
            } else if (n == NotationModel.PROP_SYMBOL_SUPPORTED) {
                updateLanguageFeatures();
                updateEnabled();
            }
        }
    }
    
    @Override
    public void removeUndoableEditListener(UndoableEditListener l) {
    }
    
    @Override
    public void addUndoableEditListener(UndoableEditListener l) {
    }
    
    @Override
    public void setModel(EntityModel newValue) {
        setModel((NotationModel) newValue);
    }

    @Nonnull
    @Override
    public JComponent getViewComponent() {
        return this;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        repetitionButtonGroup = new javax.swing.ButtonGroup();
        inversionButtonGroup = new javax.swing.ButtonGroup();
        commutationButtonGroup = new javax.swing.ButtonGroup();
        reflectionButtonGroup = new javax.swing.ButtonGroup();
        conjugationButtonGroup = new javax.swing.ButtonGroup();
        permutationButtonGroup = new javax.swing.ButtonGroup();
        rotationButtonGroup = new javax.swing.ButtonGroup();
        scrollPane = new javax.swing.JScrollPane();
        tokensPanel = new javax.swing.JPanel();
        rightLabel = new javax.swing.JLabel();
        upLabel = new javax.swing.JLabel();
        frontLabel = new javax.swing.JLabel();
        leftLabel = new javax.swing.JLabel();
        downLabel = new javax.swing.JLabel();
        backLabel = new javax.swing.JLabel();
        pCheckBox = new javax.swing.JCheckBox();
        permFacesLabel = new javax.swing.JLabel();
        permRField = new javax.swing.JTextField();
        permUField = new javax.swing.JTextField();
        permFField = new javax.swing.JTextField();
        permLField = new javax.swing.JTextField();
        permDField = new javax.swing.JTextField();
        permBField = new javax.swing.JTextField();
        signsLabel = new javax.swing.JLabel();
        plusLabel = new javax.swing.JLabel();
        permPlusField = new javax.swing.JTextField();
        plusPlusLabel = new javax.swing.JLabel();
        permPlusPlusField = new javax.swing.JTextField();
        minusLabel = new javax.swing.JLabel();
        permMinusField = new javax.swing.JTextField();
        permCycleLabel = new javax.swing.JLabel();
        permBeginLabel = new javax.swing.JLabel();
        permBeginField = new javax.swing.JTextField();
        permEndLabel = new javax.swing.JLabel();
        permEndField = new javax.swing.JTextField();
        permDelimLabel = new javax.swing.JLabel();
        permDelimField = new javax.swing.JTextField();
        permSyntaxLabel = new javax.swing.JLabel();
        permPrefixRadioButton = new javax.swing.JRadioButton();
        permPrecircumfixRadioButton = new javax.swing.JRadioButton();
        permPostcircumfixRadioButton = new javax.swing.JRadioButton();
        permSuffixRadioButton = new javax.swing.JRadioButton();
        groupingCheckBox = new javax.swing.JCheckBox();
        bracketsLabel = new javax.swing.JLabel();
        groupingBeginLabel = new javax.swing.JLabel();
        groupingBeginField = new javax.swing.JTextField();
        groupingEndLabel = new javax.swing.JLabel();
        groupingEndField = new javax.swing.JTextField();
        repetitionCheckBox = new javax.swing.JCheckBox();
        repetitionLabel = new javax.swing.JLabel();
        repetitionBeginLabel = new javax.swing.JLabel();
        repetitionBeginField = new javax.swing.JTextField();
        repetitionEndLabel = new javax.swing.JLabel();
        repetitionEndField = new javax.swing.JTextField();
        repetitionSyntaxLabel = new javax.swing.JLabel();
        repetitionPrefixRadioButton = new javax.swing.JRadioButton();
        repetitionSuffixRadioButton = new javax.swing.JRadioButton();
        inversionCheckBox = new javax.swing.JCheckBox();
        invertorLabel = new javax.swing.JLabel();
        invertorField = new javax.swing.JTextField();
        inversionSyntaxLabel = new javax.swing.JLabel();
        inversionPrefixRadioButton = new javax.swing.JRadioButton();
        inversionSuffixRadioButton = new javax.swing.JRadioButton();
        reflectionCheckBox = new javax.swing.JCheckBox();
        reflectorLabel = new javax.swing.JLabel();
        reflectorField = new javax.swing.JTextField();
        reflectionSyntaxLabel = new javax.swing.JLabel();
        reflectionPrefixRadioButton = new javax.swing.JRadioButton();
        reflectionSuffixRadioButton = new javax.swing.JRadioButton();
        conjugationCheckBox = new javax.swing.JCheckBox();
        conjugationLabel = new javax.swing.JLabel();
        conjugationBeginLabel = new javax.swing.JLabel();
        conjugationBeginField = new javax.swing.JTextField();
        conjugationEndLabel = new javax.swing.JLabel();
        conjugationEndField = new javax.swing.JTextField();
        conjugationDelimLabel = new javax.swing.JLabel();
        conjugationDelimField = new javax.swing.JTextField();
        conjugationSyntaxLabel = new javax.swing.JLabel();
        conjugationPrefixRadioButton = new javax.swing.JRadioButton();
        conjugationSuffixRadioButton = new javax.swing.JRadioButton();
        conjugationPrecircumfixRadioButton = new javax.swing.JRadioButton();
        commutationCheckBox = new javax.swing.JCheckBox();
        commutationLabel = new javax.swing.JLabel();
        commutationBeginLabel = new javax.swing.JLabel();
        commutationBeginField = new javax.swing.JTextField();
        commutationEndLabel = new javax.swing.JLabel();
        commutationEndField = new javax.swing.JTextField();
        commutationDelimLabel = new javax.swing.JLabel();
        commutationDelimField = new javax.swing.JTextField();
        commutationSyntaxLabel = new javax.swing.JLabel();
        commutationPrefixRadioButton = new javax.swing.JRadioButton();
        commutationSuffixRadioButton = new javax.swing.JRadioButton();
        commutationPrecircumfixRadioButton = new javax.swing.JRadioButton();
        rotationCheckBox = new javax.swing.JCheckBox();
        rotationLabel = new javax.swing.JLabel();
        rotationBeginLabel = new javax.swing.JLabel();
        rotationBeginField = new javax.swing.JTextField();
        rotationEndLabel = new javax.swing.JLabel();
        rotationEndField = new javax.swing.JTextField();
        rotationDelimLabel = new javax.swing.JLabel();
        rotationDelimField = new javax.swing.JTextField();
        rotationSyntaxLabel = new javax.swing.JLabel();
        rotationPrefixRadioButton = new javax.swing.JRadioButton();
        rotationSuffixRadioButton = new javax.swing.JRadioButton();
        rotationPrecircumfixRadioButton = new javax.swing.JRadioButton();
        delimitersCheckBox = new javax.swing.JCheckBox();
        delimiterLabel = new javax.swing.JLabel();
        statementDelimField = new javax.swing.JTextField();
        nopLabel = new javax.swing.JLabel();
        nopField = new javax.swing.JTextField();
        commentsCheckBox = new javax.swing.JCheckBox();
        commentMultiLineBeginLabel = new javax.swing.JLabel();
        commentMultiLineBeginField = new javax.swing.JTextField();
        commentMultiLineEndLabel = new javax.swing.JLabel();
        commentMultiLineEndField = new javax.swing.JTextField();
        commentSingleLineLabel = new javax.swing.JLabel();
        commentSingleLineBeginField = new javax.swing.JTextField();
        springPanel = new javax.swing.JPanel();
        inversionLabel = new javax.swing.JLabel();
        inversionBeginLabel = new javax.swing.JLabel();
        inversionBeginField = new javax.swing.JTextField();
        inversionEndLabel = new javax.swing.JLabel();
        inversionEndField = new javax.swing.JTextField();
        reflectionLabel = new javax.swing.JLabel();
        reflectionBeginLabel = new javax.swing.JLabel();
        reflectionBeginField = new javax.swing.JTextField();
        reflectionEndLabel = new javax.swing.JLabel();
        reflectionEndField = new javax.swing.JTextField();
        inversionCircumfixRadioButton = new javax.swing.JRadioButton();
        reflectionCircumfixRadioButton = new javax.swing.JRadioButton();
        conjugationInfixRadioButton = new javax.swing.JRadioButton();
        commutationInfixRadioButton = new javax.swing.JRadioButton();
        rotationInfixRadioButton = new javax.swing.JRadioButton();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tokensPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tokensPanel.setLayout(new java.awt.GridBagLayout());

        rightLabel.setText(labels.getString("rightTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 13, 0, 1);
        tokensPanel.add(rightLabel, gridBagConstraints);

        upLabel.setText(labels.getString("upTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        tokensPanel.add(upLabel, gridBagConstraints);

        frontLabel.setText(labels.getString("frontTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        tokensPanel.add(frontLabel, gridBagConstraints);

        leftLabel.setText(labels.getString("leftTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        tokensPanel.add(leftLabel, gridBagConstraints);

        downLabel.setText(labels.getString("downTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        tokensPanel.add(downLabel, gridBagConstraints);

        backLabel.setText(labels.getString("backTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        tokensPanel.add(backLabel, gridBagConstraints);

        pCheckBox.setText(labels.getString("permutation")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        tokensPanel.add(pCheckBox, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ch/randelshofer/cubetwister/Labels"); // NOI18N
        permFacesLabel.setText(bundle.getString("faces")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(permFacesLabel, gridBagConstraints);

        permRField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(permRField, gridBagConstraints);

        permUField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(permUField, gridBagConstraints);

        permFField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(permFField, gridBagConstraints);

        permLField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(permLField, gridBagConstraints);

        permDField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(permDField, gridBagConstraints);

        permBField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(permBField, gridBagConstraints);

        signsLabel.setText(bundle.getString("orientationTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(signsLabel, gridBagConstraints);

        plusLabel.setText(bundle.getString("plus")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 0);
        tokensPanel.add(plusLabel, gridBagConstraints);

        permPlusField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(permPlusField, gridBagConstraints);

        plusPlusLabel.setText(bundle.getString("plusPlus")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 40;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(plusPlusLabel, gridBagConstraints);

        permPlusPlusField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(permPlusPlusField, gridBagConstraints);

        minusLabel.setText(bundle.getString("minus")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 0);
        tokensPanel.add(minusLabel, gridBagConstraints);

        permMinusField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(permMinusField, gridBagConstraints);

        permCycleLabel.setText(bundle.getString("cycleTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(permCycleLabel, gridBagConstraints);

        permBeginLabel.setText(bundle.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 50;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 0);
        tokensPanel.add(permBeginLabel, gridBagConstraints);

        permBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(permBeginField, gridBagConstraints);

        permEndLabel.setText(bundle.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 50;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(permEndLabel, gridBagConstraints);

        permEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 50;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(permEndField, gridBagConstraints);

        permDelimLabel.setText(bundle.getString("delimiterTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 0);
        tokensPanel.add(permDelimLabel, gridBagConstraints);

        permDelimField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(permDelimField, gridBagConstraints);

        permSyntaxLabel.setText(bundle.getString("syntaxLabel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 70;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(permSyntaxLabel, gridBagConstraints);

        permutationButtonGroup.add(permPrefixRadioButton);
        permPrefixRadioButton.setText(bundle.getString("permPrefixRadio")); // NOI18N
        permPrefixRadioButton.setToolTipText(labels.getString("beforeCycleBeginTip")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 70;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        tokensPanel.add(permPrefixRadioButton, gridBagConstraints);

        permutationButtonGroup.add(permPrecircumfixRadioButton);
        permPrecircumfixRadioButton.setText(bundle.getString("permPrecircumfixRadio")); // NOI18N
        permPrecircumfixRadioButton.setToolTipText(labels.getString("afterCycleBeginTip")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 70;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(permPrecircumfixRadioButton, gridBagConstraints);

        permutationButtonGroup.add(permPostcircumfixRadioButton);
        permPostcircumfixRadioButton.setText(bundle.getString("permPostcircumfixRadio")); // NOI18N
        permPostcircumfixRadioButton.setToolTipText(labels.getString("beforeCycleEndTip")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 70;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(permPostcircumfixRadioButton, gridBagConstraints);

        permutationButtonGroup.add(permSuffixRadioButton);
        permSuffixRadioButton.setText(bundle.getString("permSuffixRadio")); // NOI18N
        permSuffixRadioButton.setToolTipText(labels.getString("afterCycleEndTip")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 70;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(permSuffixRadioButton, gridBagConstraints);

        groupingCheckBox.setText(labels.getString("sequence")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 80;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        tokensPanel.add(groupingCheckBox, gridBagConstraints);

        bracketsLabel.setText(bundle.getString("sequenceTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 90;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(bracketsLabel, gridBagConstraints);

        groupingBeginLabel.setText(bundle.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 90;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        tokensPanel.add(groupingBeginLabel, gridBagConstraints);

        groupingBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 90;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(groupingBeginField, gridBagConstraints);

        groupingEndLabel.setText(bundle.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 90;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(groupingEndLabel, gridBagConstraints);

        groupingEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 90;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(groupingEndField, gridBagConstraints);

        repetitionCheckBox.setText(labels.getString("repetition")); // NOI18N
        repetitionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repetitionCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 100;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        tokensPanel.add(repetitionCheckBox, gridBagConstraints);

        repetitionLabel.setText(bundle.getString("repetition")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 110;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(repetitionLabel, gridBagConstraints);

        repetitionBeginLabel.setText(bundle.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 110;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(repetitionBeginLabel, gridBagConstraints);

        repetitionBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 110;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(repetitionBeginField, gridBagConstraints);

        repetitionEndLabel.setText(bundle.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 110;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(repetitionEndLabel, gridBagConstraints);

        repetitionEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 110;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(repetitionEndField, gridBagConstraints);

        repetitionSyntaxLabel.setText(bundle.getString("position")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(repetitionSyntaxLabel, gridBagConstraints);

        repetitionButtonGroup.add(repetitionPrefixRadioButton);
        repetitionPrefixRadioButton.setText(bundle.getString("prefixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 120;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        tokensPanel.add(repetitionPrefixRadioButton, gridBagConstraints);

        repetitionButtonGroup.add(repetitionSuffixRadioButton);
        repetitionSuffixRadioButton.setText(bundle.getString("suffixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 120;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(repetitionSuffixRadioButton, gridBagConstraints);

        inversionCheckBox.setText(labels.getString("inversionTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 130;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        tokensPanel.add(inversionCheckBox, gridBagConstraints);

        invertorLabel.setText(bundle.getString("invertorTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(invertorLabel, gridBagConstraints);

        invertorField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(invertorField, gridBagConstraints);

        inversionSyntaxLabel.setText(bundle.getString("position")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 160;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(inversionSyntaxLabel, gridBagConstraints);

        inversionButtonGroup.add(inversionPrefixRadioButton);
        inversionPrefixRadioButton.setText(bundle.getString("prefixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 160;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        tokensPanel.add(inversionPrefixRadioButton, gridBagConstraints);

        inversionButtonGroup.add(inversionSuffixRadioButton);
        inversionSuffixRadioButton.setText(bundle.getString("suffixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 160;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(inversionSuffixRadioButton, gridBagConstraints);

        reflectionCheckBox.setText(labels.getString("reflection")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 170;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        tokensPanel.add(reflectionCheckBox, gridBagConstraints);

        reflectorLabel.setText(bundle.getString("reflectorTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 190;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(reflectorLabel, gridBagConstraints);

        reflectorField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 190;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(reflectorField, gridBagConstraints);

        reflectionSyntaxLabel.setText(bundle.getString("position")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(reflectionSyntaxLabel, gridBagConstraints);

        reflectionButtonGroup.add(reflectionPrefixRadioButton);
        reflectionPrefixRadioButton.setText(bundle.getString("prefixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 200;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        tokensPanel.add(reflectionPrefixRadioButton, gridBagConstraints);

        reflectionButtonGroup.add(reflectionSuffixRadioButton);
        reflectionSuffixRadioButton.setText(bundle.getString("suffixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 200;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(reflectionSuffixRadioButton, gridBagConstraints);

        conjugationCheckBox.setText(labels.getString("conjugationTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 210;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        tokensPanel.add(conjugationCheckBox, gridBagConstraints);

        conjugationLabel.setText(labels.getString("conjugation")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 220;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(conjugationLabel, gridBagConstraints);

        conjugationBeginLabel.setText(labels.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 220;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(conjugationBeginLabel, gridBagConstraints);

        conjugationBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 220;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(conjugationBeginField, gridBagConstraints);

        conjugationEndLabel.setText(labels.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 220;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(conjugationEndLabel, gridBagConstraints);

        conjugationEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 220;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(conjugationEndField, gridBagConstraints);

        conjugationDelimLabel.setText(labels.getString("delimiter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 230;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 0);
        tokensPanel.add(conjugationDelimLabel, gridBagConstraints);

        conjugationDelimField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 230;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(conjugationDelimField, gridBagConstraints);

        conjugationSyntaxLabel.setText(labels.getString("positionTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 240;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(conjugationSyntaxLabel, gridBagConstraints);

        conjugationButtonGroup.add(conjugationPrefixRadioButton);
        conjugationPrefixRadioButton.setText(labels.getString("prefixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 240;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        tokensPanel.add(conjugationPrefixRadioButton, gridBagConstraints);

        conjugationButtonGroup.add(conjugationSuffixRadioButton);
        conjugationSuffixRadioButton.setText(labels.getString("suffixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 240;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(conjugationSuffixRadioButton, gridBagConstraints);

        conjugationButtonGroup.add(conjugationPrecircumfixRadioButton);
        conjugationPrecircumfixRadioButton.setText(labels.getString("enclosingRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 240;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(conjugationPrecircumfixRadioButton, gridBagConstraints);

        commutationCheckBox.setText(labels.getString("commutation")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 250;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        tokensPanel.add(commutationCheckBox, gridBagConstraints);

        commutationLabel.setText(labels.getString("commutation")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 260;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(commutationLabel, gridBagConstraints);

        commutationBeginLabel.setText(labels.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 260;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(commutationBeginLabel, gridBagConstraints);

        commutationBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 260;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(commutationBeginField, gridBagConstraints);

        commutationEndLabel.setText(labels.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 260;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(commutationEndLabel, gridBagConstraints);

        commutationEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 260;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(commutationEndField, gridBagConstraints);

        commutationDelimLabel.setText(labels.getString("delimiter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 270;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 0);
        tokensPanel.add(commutationDelimLabel, gridBagConstraints);

        commutationDelimField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 270;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(commutationDelimField, gridBagConstraints);

        commutationSyntaxLabel.setText(labels.getString("position")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 280;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(commutationSyntaxLabel, gridBagConstraints);

        commutationButtonGroup.add(commutationPrefixRadioButton);
        commutationPrefixRadioButton.setText(labels.getString("prefixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 280;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        tokensPanel.add(commutationPrefixRadioButton, gridBagConstraints);

        commutationButtonGroup.add(commutationSuffixRadioButton);
        commutationSuffixRadioButton.setText(labels.getString("suffixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 280;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(commutationSuffixRadioButton, gridBagConstraints);

        commutationButtonGroup.add(commutationPrecircumfixRadioButton);
        commutationPrecircumfixRadioButton.setText(labels.getString("enclosingRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 280;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(commutationPrecircumfixRadioButton, gridBagConstraints);

        rotationCheckBox.setText(labels.getString("rotationTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 290;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        tokensPanel.add(rotationCheckBox, gridBagConstraints);

        rotationLabel.setText(labels.getString("rotation")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(rotationLabel, gridBagConstraints);

        rotationBeginLabel.setText(labels.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 300;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(rotationBeginLabel, gridBagConstraints);

        rotationBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(rotationBeginField, gridBagConstraints);

        rotationEndLabel.setText(labels.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 300;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(rotationEndLabel, gridBagConstraints);

        rotationEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 300;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(rotationEndField, gridBagConstraints);

        rotationDelimLabel.setText(labels.getString("delimiter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 310;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 0);
        tokensPanel.add(rotationDelimLabel, gridBagConstraints);

        rotationDelimField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 310;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(rotationDelimField, gridBagConstraints);

        rotationSyntaxLabel.setText(labels.getString("positionTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 320;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(rotationSyntaxLabel, gridBagConstraints);

        rotationButtonGroup.add(rotationPrefixRadioButton);
        rotationPrefixRadioButton.setText(labels.getString("prefixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 320;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        tokensPanel.add(rotationPrefixRadioButton, gridBagConstraints);

        rotationButtonGroup.add(rotationSuffixRadioButton);
        rotationSuffixRadioButton.setText(labels.getString("suffixRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 320;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(rotationSuffixRadioButton, gridBagConstraints);

        rotationButtonGroup.add(rotationPrecircumfixRadioButton);
        rotationPrecircumfixRadioButton.setText(labels.getString("enclosingRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 320;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(rotationPrecircumfixRadioButton, gridBagConstraints);

        delimitersCheckBox.setText(labels.getString("delimiterTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 330;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        tokensPanel.add(delimitersCheckBox, gridBagConstraints);

        delimiterLabel.setText(labels.getString("statementDelimiter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 340;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(delimiterLabel, gridBagConstraints);

        statementDelimField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 340;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(statementDelimField, gridBagConstraints);

        nopLabel.setText(labels.getString("nop")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 340;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(nopLabel, gridBagConstraints);

        nopField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 340;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(nopField, gridBagConstraints);

        commentsCheckBox.setText(labels.getString("commentsTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 350;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        tokensPanel.add(commentsCheckBox, gridBagConstraints);

        commentMultiLineBeginLabel.setText(labels.getString("multilineCommentBegin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 360;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(commentMultiLineBeginLabel, gridBagConstraints);

        commentMultiLineBeginField.setColumns(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 360;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(commentMultiLineBeginField, gridBagConstraints);

        commentMultiLineEndLabel.setText(labels.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 360;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(commentMultiLineEndLabel, gridBagConstraints);

        commentMultiLineEndField.setColumns(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 360;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(commentMultiLineEndField, gridBagConstraints);

        commentSingleLineLabel.setText(labels.getString("singleLineComment")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 370;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokensPanel.add(commentSingleLineLabel, gridBagConstraints);

        commentSingleLineBeginField.setColumns(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 370;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        tokensPanel.add(commentSingleLineBeginField, gridBagConstraints);

        springPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 100;
        gridBagConstraints.gridy = 500;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        tokensPanel.add(springPanel, gridBagConstraints);

        inversionLabel.setText(bundle.getString("inversionTitle")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 140;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(inversionLabel, gridBagConstraints);

        inversionBeginLabel.setText(bundle.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 140;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(inversionBeginLabel, gridBagConstraints);

        inversionBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 140;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(inversionBeginField, gridBagConstraints);

        inversionEndLabel.setText(bundle.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 140;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(inversionEndLabel, gridBagConstraints);

        inversionEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 140;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(inversionEndField, gridBagConstraints);

        reflectionLabel.setText(bundle.getString("repetition")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tokensPanel.add(reflectionLabel, gridBagConstraints);

        reflectionBeginLabel.setText(bundle.getString("begin")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 180;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(reflectionBeginLabel, gridBagConstraints);

        reflectionBeginField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(reflectionBeginField, gridBagConstraints);

        reflectionEndLabel.setText(bundle.getString("end")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 180;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        tokensPanel.add(reflectionEndLabel, gridBagConstraints);

        reflectionEndField.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 180;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        tokensPanel.add(reflectionEndField, gridBagConstraints);

        inversionButtonGroup.add(inversionCircumfixRadioButton);
        inversionCircumfixRadioButton.setText(bundle.getString("enclosingRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 160;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(inversionCircumfixRadioButton, gridBagConstraints);

        reflectionButtonGroup.add(reflectionCircumfixRadioButton);
        reflectionCircumfixRadioButton.setText(bundle.getString("enclosingRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 200;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(reflectionCircumfixRadioButton, gridBagConstraints);

        conjugationButtonGroup.add(conjugationInfixRadioButton);
        conjugationInfixRadioButton.setText(labels.getString("betweenRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 240;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(conjugationInfixRadioButton, gridBagConstraints);

        commutationButtonGroup.add(commutationInfixRadioButton);
        commutationInfixRadioButton.setText(labels.getString("betweenRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 280;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(commutationInfixRadioButton, gridBagConstraints);

        rotationButtonGroup.add(rotationInfixRadioButton);
        rotationInfixRadioButton.setText(labels.getString("betweenRadio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 320;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        tokensPanel.add(rotationInfixRadioButton, gridBagConstraints);

        scrollPane.setViewportView(tokensPanel);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void repetitionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repetitionCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_repetitionCheckBoxActionPerformed
                                                
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backLabel;
    private javax.swing.JLabel bracketsLabel;
    private javax.swing.JTextField commentMultiLineBeginField;
    private javax.swing.JLabel commentMultiLineBeginLabel;
    private javax.swing.JTextField commentMultiLineEndField;
    private javax.swing.JLabel commentMultiLineEndLabel;
    private javax.swing.JTextField commentSingleLineBeginField;
    private javax.swing.JLabel commentSingleLineLabel;
    private javax.swing.JCheckBox commentsCheckBox;
    private javax.swing.JTextField commutationBeginField;
    private javax.swing.JLabel commutationBeginLabel;
    private javax.swing.ButtonGroup commutationButtonGroup;
    private javax.swing.JCheckBox commutationCheckBox;
    private javax.swing.JTextField commutationDelimField;
    private javax.swing.JLabel commutationDelimLabel;
    private javax.swing.JTextField commutationEndField;
    private javax.swing.JLabel commutationEndLabel;
    private javax.swing.JRadioButton commutationInfixRadioButton;
    private javax.swing.JLabel commutationLabel;
    private javax.swing.JRadioButton commutationPrecircumfixRadioButton;
    private javax.swing.JRadioButton commutationPrefixRadioButton;
    private javax.swing.JRadioButton commutationSuffixRadioButton;
    private javax.swing.JLabel commutationSyntaxLabel;
    private javax.swing.JTextField conjugationBeginField;
    private javax.swing.JLabel conjugationBeginLabel;
    private javax.swing.ButtonGroup conjugationButtonGroup;
    private javax.swing.JCheckBox conjugationCheckBox;
    private javax.swing.JTextField conjugationDelimField;
    private javax.swing.JLabel conjugationDelimLabel;
    private javax.swing.JTextField conjugationEndField;
    private javax.swing.JLabel conjugationEndLabel;
    private javax.swing.JRadioButton conjugationInfixRadioButton;
    private javax.swing.JLabel conjugationLabel;
    private javax.swing.JRadioButton conjugationPrecircumfixRadioButton;
    private javax.swing.JRadioButton conjugationPrefixRadioButton;
    private javax.swing.JRadioButton conjugationSuffixRadioButton;
    private javax.swing.JLabel conjugationSyntaxLabel;
    private javax.swing.JLabel delimiterLabel;
    private javax.swing.JCheckBox delimitersCheckBox;
    private javax.swing.JLabel downLabel;
    private javax.swing.JLabel frontLabel;
    private javax.swing.JTextField groupingBeginField;
    private javax.swing.JLabel groupingBeginLabel;
    private javax.swing.JCheckBox groupingCheckBox;
    private javax.swing.JTextField groupingEndField;
    private javax.swing.JLabel groupingEndLabel;
    private javax.swing.JTextField inversionBeginField;
    private javax.swing.JLabel inversionBeginLabel;
    private javax.swing.ButtonGroup inversionButtonGroup;
    private javax.swing.JCheckBox inversionCheckBox;
    private javax.swing.JRadioButton inversionCircumfixRadioButton;
    private javax.swing.JTextField inversionEndField;
    private javax.swing.JLabel inversionEndLabel;
    private javax.swing.JLabel inversionLabel;
    private javax.swing.JRadioButton inversionPrefixRadioButton;
    private javax.swing.JRadioButton inversionSuffixRadioButton;
    private javax.swing.JLabel inversionSyntaxLabel;
    private javax.swing.JTextField invertorField;
    private javax.swing.JLabel invertorLabel;
    private javax.swing.JLabel leftLabel;
    private javax.swing.JLabel minusLabel;
    private javax.swing.JTextField nopField;
    private javax.swing.JLabel nopLabel;
    private javax.swing.JCheckBox pCheckBox;
    private javax.swing.JTextField permBField;
    private javax.swing.JTextField permBeginField;
    private javax.swing.JLabel permBeginLabel;
    private javax.swing.JLabel permCycleLabel;
    private javax.swing.JTextField permDField;
    private javax.swing.JTextField permDelimField;
    private javax.swing.JLabel permDelimLabel;
    private javax.swing.JTextField permEndField;
    private javax.swing.JLabel permEndLabel;
    private javax.swing.JTextField permFField;
    private javax.swing.JLabel permFacesLabel;
    private javax.swing.JTextField permLField;
    private javax.swing.JTextField permMinusField;
    private javax.swing.JTextField permPlusField;
    private javax.swing.JTextField permPlusPlusField;
    private javax.swing.JRadioButton permPostcircumfixRadioButton;
    private javax.swing.JRadioButton permPrecircumfixRadioButton;
    private javax.swing.JRadioButton permPrefixRadioButton;
    private javax.swing.JTextField permRField;
    private javax.swing.JRadioButton permSuffixRadioButton;
    private javax.swing.JLabel permSyntaxLabel;
    private javax.swing.JTextField permUField;
    private javax.swing.ButtonGroup permutationButtonGroup;
    private javax.swing.JLabel plusLabel;
    private javax.swing.JLabel plusPlusLabel;
    private javax.swing.JTextField reflectionBeginField;
    private javax.swing.JLabel reflectionBeginLabel;
    private javax.swing.ButtonGroup reflectionButtonGroup;
    private javax.swing.JCheckBox reflectionCheckBox;
    private javax.swing.JRadioButton reflectionCircumfixRadioButton;
    private javax.swing.JTextField reflectionEndField;
    private javax.swing.JLabel reflectionEndLabel;
    private javax.swing.JLabel reflectionLabel;
    private javax.swing.JRadioButton reflectionPrefixRadioButton;
    private javax.swing.JRadioButton reflectionSuffixRadioButton;
    private javax.swing.JLabel reflectionSyntaxLabel;
    private javax.swing.JTextField reflectorField;
    private javax.swing.JLabel reflectorLabel;
    private javax.swing.JTextField repetitionBeginField;
    private javax.swing.JLabel repetitionBeginLabel;
    private javax.swing.ButtonGroup repetitionButtonGroup;
    private javax.swing.JCheckBox repetitionCheckBox;
    private javax.swing.JTextField repetitionEndField;
    private javax.swing.JLabel repetitionEndLabel;
    private javax.swing.JLabel repetitionLabel;
    private javax.swing.JRadioButton repetitionPrefixRadioButton;
    private javax.swing.JRadioButton repetitionSuffixRadioButton;
    private javax.swing.JLabel repetitionSyntaxLabel;
    private javax.swing.JLabel rightLabel;
    private javax.swing.JTextField rotationBeginField;
    private javax.swing.JLabel rotationBeginLabel;
    private javax.swing.ButtonGroup rotationButtonGroup;
    private javax.swing.JCheckBox rotationCheckBox;
    private javax.swing.JTextField rotationDelimField;
    private javax.swing.JLabel rotationDelimLabel;
    private javax.swing.JTextField rotationEndField;
    private javax.swing.JLabel rotationEndLabel;
    private javax.swing.JRadioButton rotationInfixRadioButton;
    private javax.swing.JLabel rotationLabel;
    private javax.swing.JRadioButton rotationPrecircumfixRadioButton;
    private javax.swing.JRadioButton rotationPrefixRadioButton;
    private javax.swing.JRadioButton rotationSuffixRadioButton;
    private javax.swing.JLabel rotationSyntaxLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JLabel signsLabel;
    private javax.swing.JPanel springPanel;
    private javax.swing.JTextField statementDelimField;
    private javax.swing.JPanel tokensPanel;
    private javax.swing.JLabel upLabel;
    // End of variables declaration//GEN-END:variables
    
}
