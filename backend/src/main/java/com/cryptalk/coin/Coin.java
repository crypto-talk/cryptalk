package com.cryptalk.coin;

import jakarta.persistence.*;

@Entity
@Table(name = "coins")
public class Coin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 20)
    private String symbol;
    @Column(nullable = false, length = 80)
    private String name;
    @Column(name = "chain_type", nullable = false, length = 20)
    private String chainType;
    @Column(name = "contract_address", length = 80)
    private String contractAddress;
    @Column(name = "accent_color", nullable = false, length = 20)
    private String accentColor;
    @Column(name = "display_order", nullable = false)
    private int displayOrder;
    @Column(nullable = false)
    private boolean active;
    protected Coin() {}
    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getChainType() { return chainType; }
    public String getAccentColor() { return accentColor; }
}
