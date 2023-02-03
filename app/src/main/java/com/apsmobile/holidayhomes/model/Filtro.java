package com.apsmobile.holidayhomes.model;

import java.io.Serializable;

public class Filtro implements Serializable {

    private int qtdQuarto;
    private int qtdBanheiro;
    private int qtdGaragem;

    public int getQtdQuarto() {
        return qtdQuarto;
    }

    public void setQtdQuarto(int qtdQuarto) {
        this.qtdQuarto = qtdQuarto;
    }

    public int getQtdBanheiro() {
        return qtdBanheiro;
    }

    public void setQtdBanheiro(int qtdBanheiro) {
        this.qtdBanheiro = qtdBanheiro;
    }

    public int getQtdGaragem() {
        return qtdGaragem;
    }

    public void setQtdGaragem(int qtdGaragem) {
        this.qtdGaragem = qtdGaragem;
    }
}
