package network;

import bloom.BloomFilter;
import compress.Compressor;
import hash.HashFunction;

import java.util.ArrayList;
import java.util.List;

public class NetworkSimulator {
    private final List<Node> nodes = new ArrayList<>();

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void broadcastFull(int senderId) {
        Node sender = getNodeById(senderId);
        Message msg = sender.sendFullFilter();

        for (Node receiver : nodes) {
            if (receiver.getId() != senderId) {
                receiver.receive(msg);
            }
        }

        System.out.println("Broadcasted FULL FILTER from Node " + senderId + " (size = " + msg.data.length + " bytes)");
    }

    public void broadcastDelta(int senderId) {
        Node sender = getNodeById(senderId);

        for (Node receiver : nodes) {
            Message delta = sender.sendDelta(receiver);
            receiver.receive(delta);
            System.out.println("Sent DELTA from Node " + senderId + " to Node " + receiver.getId() + " (size = " + delta.data.length + " bytes)");
        }
    }

    private Node getNodeById(int id) {
        return nodes.stream().filter(n -> n.getId() == id).findFirst().orElse(null);
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
