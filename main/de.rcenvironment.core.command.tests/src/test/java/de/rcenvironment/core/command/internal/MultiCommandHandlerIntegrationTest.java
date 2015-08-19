/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.command.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rcenvironment.core.authentication.Session;
import de.rcenvironment.core.command.common.CommandException;
import de.rcenvironment.core.command.internal.handlers.BuiltInCommandPlugin;
import de.rcenvironment.core.utils.common.textstream.TextOutputReceiver;

/**
 * Integration test for {@link MultiCommandHandler}. The basic test setup consists of providing mocked/stubbed service dependencies (if
 * needed), sending tokenized commands and then verifying the expected output and/or service calls.
 * 
 * @author Robert Mischke
 * @author Sascha Zur
 * @author Jan Flink
 */
public class MultiCommandHandlerIntegrationTest {

    private static final String EXPLAIN_COMMAND_TOKEN = "explain";

    private static final String UNEXPECTED_REPONSE_TEXT = "Unexpected reponse text: ";

    private CommandPluginDispatcher commandPluginDispatcher;

    /**
     * Creates a Session for testing.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        Session.create("dummyUser", 1);
    }

    /**
     * Common test setup.
     */
    @Before
    public void setUp() {
        commandPluginDispatcher = new CommandPluginDispatcher();
        commandPluginDispatcher.registerPlugin(new BuiltInCommandPlugin());
    }

    /**
     * Test sending an empty list of tokens. The expected behaviour is an exception that signals that help output should be presented to the
     * user.
     */
    @Test
    public void testEmptyTokenList() {

        List<String> tokens = new ArrayList<String>();
        TextOutputReceiver outputReceiver = EasyMock.createStrictMock(TextOutputReceiver.class);

        // define mock expectation
        outputReceiver.onStart();
        Capture<CommandException> capture = new Capture<CommandException>();
        outputReceiver.onFatalError(EasyMock.capture(capture));

        EasyMock.replay(outputReceiver);

        // invoke
        new MultiCommandHandler(tokens, outputReceiver, commandPluginDispatcher).call();
        // test callback parameter(s)
        assertEquals("Unexpected CommandException sub-type", CommandException.Type.HELP_REQUESTED, capture.getValue().getType());

        EasyMock.verify(outputReceiver);
    }

    /**
     * Test sending the "dummy" command. The expected behaviour is a response line containing the word "dummy" (case insensitive), followed
     * by an "end-of-output" marker.
     */
    @Test
    public void testDummyCommand() {
        List<String> tokens = new ArrayList<String>();
        tokens.add("dummy");
        TextOutputReceiver outputReceiver = EasyMock.createStrictMock(TextOutputReceiver.class);

        // define mock expectation
        outputReceiver.onStart();
        Capture<String> capture = new Capture<String>();
        outputReceiver.addOutput(EasyMock.capture(capture));
        outputReceiver.onFinished();

        EasyMock.replay(outputReceiver);

        // invoke
        new MultiCommandHandler(tokens, outputReceiver, commandPluginDispatcher).call();
        // test callback parameter(s)
        String capturedText = capture.getValue();
        assertTrue("Unexpected reponse text (should contain 'dummy')", capturedText.toLowerCase().contains("dummy"));

        EasyMock.verify(outputReceiver);
    }

    /**
     * Tests whether quoted strings that were split into separate tokens are properly re-assembled, and if empty tokens (resulting from
     * multi-whitespace splitting) are properly discarded.
     */
    @Test
    public void testTokenQuotingNormalization() {

        List<String> tokens = Arrays.asList(new String[] {
            EXPLAIN_COMMAND_TOKEN, "a", "\"b", "2", "", "", "c\"", "d"
        });
        String capturedText = runWithTokens(tokens);
        assertTrue(UNEXPECTED_REPONSE_TEXT + capturedText, capturedText.toLowerCase().contains("[a, b 2 c, d]"));
    }

    /**
     * Tests if unfinished quoted parts like <code>test "a b c</code> are properly rejected.
     */
    @Test
    public void testTokenQuotingFailureOnUnfinishedSequence() {

        List<String> tokens = Arrays.asList(new String[] {
            EXPLAIN_COMMAND_TOKEN, "a", "\"b", "2", "", "", "c\"", "d", "\"start", "addition"
        });
        String capturedText = runWithTokens(tokens);
        assertTrue(UNEXPECTED_REPONSE_TEXT + capturedText, capturedText.toLowerCase().contains("error: "));
        assertTrue(UNEXPECTED_REPONSE_TEXT + capturedText, capturedText.toLowerCase().contains(": start addition"));
    }

    /**
     * Tests unusual, but accepted uses of quotes.
     */
    @Test
    public void testTokenQuotingSpecialCases() {

        List<String> tokens = Arrays.asList(new String[] {
            EXPLAIN_COMMAND_TOKEN, "a\"b", "\"c", "d\"e", "\"", "f\"g"
        });
        String capturedText = runWithTokens(tokens);
        assertTrue(UNEXPECTED_REPONSE_TEXT + capturedText, capturedText.toLowerCase().contains("[a\"b, c d\"e , f\"g]"));
    }

    /**
     * Tests that escaped double-quotes are properly unescaped.
     */
    @Test
    public void testTokenQuotesUnescaping() {

        List<String> tokens = Arrays.asList(new String[] {
            EXPLAIN_COMMAND_TOKEN, "a\\\"", "\\\"b", "c\\\"d"
        });
        String capturedText = runWithTokens(tokens);
        assertTrue(UNEXPECTED_REPONSE_TEXT + capturedText, capturedText.toLowerCase().contains("[a\", \"b, c\"d]"));
    }

    private String runWithTokens(List<String> tokens) {
        TextOutputReceiver outputReceiver = EasyMock.createStrictMock(TextOutputReceiver.class);

        // define mock expectation
        outputReceiver.onStart();
        Capture<String> capture = new Capture<String>();
        outputReceiver.addOutput(EasyMock.capture(capture));
        outputReceiver.onFinished();

        EasyMock.replay(outputReceiver);

        // invoke
        new MultiCommandHandler(tokens, outputReceiver, commandPluginDispatcher).call();
        // test callback parameter(s)
        String capturedText = capture.getValue();
        EasyMock.verify(outputReceiver);
        return capturedText;
    }

}
