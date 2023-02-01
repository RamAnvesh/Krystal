package com.flipkart.krystal.krystex.commands;

import com.flipkart.krystal.krystex.node.DependantChain;
import com.flipkart.krystal.krystex.node.NodeId;

public record Close(NodeId nodeId, DependantChain nodeDependants) implements NodeCommand {}
