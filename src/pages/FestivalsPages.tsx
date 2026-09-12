import { Container, Typography, Box } from '@mui/material';
import { FestivalFilterGrid } from '../components/FestivalFilterGrid';

export function FestivalsPage() {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold' }} gutterBottom>
          Festival Cinematografici
        </Typography>
        <Typography variant="subtitle1" color="text.secondary">
          Esplora tutti i festival ed effettua ricerche per nome
        </Typography>
      </Box>

      <FestivalFilterGrid />
    </Container>
  );
}