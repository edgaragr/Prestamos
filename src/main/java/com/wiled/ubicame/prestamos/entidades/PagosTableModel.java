 private class PagosTableModel extends AbstractTableModel {

        List<Pago> pagos = null;
        String[] columns = {"Tipo Pago", "Monto", "Mora", "Fecha"};

        public PagosTableModel() {
            pagos = new ArrayList<Pago>();
        }

        public PagosTableModel(List<Pago> pagos) {
            this.pagos = pagos;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public int getRowCount() {
            return pagos.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object value = null;
            switch (columnIndex) {
                case 0:
                    value = pagos.get(rowIndex).getTipoPago();
                    break;
                case 1:
                    value = pagos.get(rowIndex).getMonto();
                    break;
                case 2:
                    value = pagos.get(rowIndex).getMora();
                    break;
                case 3:
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    value = sdf.format(pagos.get(rowIndex).getFecha());
                    break;
            }

            return value; 
        }
    }