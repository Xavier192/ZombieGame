/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Xavier
 */
public class Looby extends JPanel implements ActionListener {

    private Map<String, String> nombresJugadores;
    private DefaultTableModel modeloTabla;
    private JTable tablaLooby;
    private JLabel errorMessage;
    private ZombieGame zombieGameLooby;
    private Image background;

    public Looby(ZombieGame zombieGameLobby) {
        this.zombieGameLooby = zombieGameLobby;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Looby", TitledBorder.CENTER, TitledBorder.TOP));
        setLayout(new GridBagLayout());
        this.modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tablaLooby = new JTable(this.modeloTabla) {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension(600, 400);
            }
        };
        this.modeloTabla.addColumn("Nombre");
        this.modeloTabla.addColumn("IP");
        this.nombresJugadores = new HashMap<String, String>();
        this.errorMessage = new JLabel("");
        setBackground("img/fondo2.png");
        configurarVentana();
    }

    public void configurarVentana() {
        removeAll();
        JButton botonIniciar = new JButton("Iniciar partida");
        JLabel marcadores = new JLabel("Marcadores última partida");
        JLabel confServ = new JLabel("Configuración del servidor");
        marcadores.setFont(marcadores.getFont().deriveFont(14.0f));
        confServ.setFont(confServ.getFont().deriveFont(14.0f));

        botonIniciar.addActionListener(this);
        GridBagConstraints c = configurarConstraints(1000, 0, 600, 400, 0, 0);
        add(new JScrollPane(this.tablaLooby), c);
        c = configurarConstraints(0, 0, 100, 10, 80, 0);
        add(confServ, c);
        c = configurarConstraints(0, 100, 100, 10, 110, 30);
        add(new JLabel("IP: " + this.zombieGameLooby.getLocalIp()), c);
        c = configurarConstraints(0, 110, 100, 10, 135, 10);
        add(new JLabel("Port: " + this.zombieGameLooby.getServer().getPort()), c);
        c = configurarConstraints(0, 120, 10, 10, 40, 15);
        add(marcadores, c);
        c = configurarConstraints(0, 130, 10, 10, 95, 10);
        add(new JLabel("Equipo A: " + this.zombieGameLooby.getScore().getPuntosEquipoA()), c);
        c = configurarConstraints(0, 140, 10, 10, 95, 10);
        add(new JLabel("Equipo B: " + this.zombieGameLooby.getScore().getPuntosEquipoB()), c);
        c = configurarConstraints(0, 150, 10, 10, 50, 10);
        add(botonIniciar, c);
        c = configurarConstraints(0, 160, 10, 10, 0, 10);
        add(this.errorMessage, c);
    }

    public GridBagConstraints configurarConstraints(int x, int y, int width, int height, int ipadX, int ipadY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.ipadx = ipadX;
        constraints.ipady = ipadY;

        return constraints;
    }

    public void addPlayer(String name, String id) {
        this.nombresJugadores.put(id, name);
        updatePanel();
    }

    public void removePlayer(String id) {
        this.nombresJugadores.remove(id);
        updatePanel();
    }

    public void updatePanel() {
        Object[] fila = new Object[2];
        cleanJTable();
        for (Map.Entry<String, String> entry : this.nombresJugadores.entrySet()) {
            fila[0] = entry.getValue();
            fila[1] = entry.getKey();
            this.modeloTabla.addRow(fila);
        }
    }

    public void cleanJTable() {
        int rowCount = this.modeloTabla.getRowCount();
        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            this.modeloTabla.removeRow(i);
        }
    }

    public void paintComponent(Graphics g) {

        /* Obtenemos el tamaño del panel para hacer que se ajuste a este
		cada vez que redimensionemos la ventana y se lo pasamos al drawImage */
        int width = this.getSize().width;
        int height = this.getSize().height;

        // Mandamos que pinte la imagen en el panel
        if (this.background != null) {
            g.drawImage(this.background, 0, 0, width, height, null);
        }

        super.paintComponent(g);
    }
    // Metodo donde le pasaremos la dirección de la imagen a cargar.

    public void setBackground(String imagePath) {

        // Construimos la imagen y se la asignamos al atributo background.
        this.setOpaque(false);
        this.background = new ImageIcon(imagePath).getImage();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (this.nombresJugadores.size() < 1 ) {
            this.errorMessage.setText("Error: No hay suficientes jugadores");
        } else {
            (new Thread(this.zombieGameLooby)).start();
        }
    }
}
