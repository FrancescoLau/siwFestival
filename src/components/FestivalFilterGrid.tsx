import { useEffect, useState } from 'react';
import {
  Box,
  TextField,
  Grid,
  Card,
  CardContent,
  Typography,
  CircularProgress,
  Alert,
  InputAdornment
} from '@mui/material';
import type { Festival } from '../types';
import { getFestivals } from '../services/festivalService';

export function FestivalFilterGrid() {
  const [allFestivals, setAllFestivals] = useState<Festival[]>([]);
  const [searchName, setSearchName] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getFestivals()
      .then((data) => {
        setAllFestivals(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError('Impossibile caricare la lista dei festival');
        setLoading(false);
      });
  }, []);

  // Filtro in-memory sul nome (case-insensitive)
  const filteredFestivals = allFestivals.filter((festival) =>
    festival.nome.toLowerCase().includes(searchName.toLowerCase().trim())
  );

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ py: 3 }}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ width: '100%', py: 2 }}>
      {/* Campo di ricerca controllato */}
      <TextField
        fullWidth
        variant="outlined"
        label="Cerca festival per nome..."
        value={searchName}
        onChange={(e) => setSearchName(e.target.value)}
        sx={{ mb: 4 }}
        slotProps={{
          input: {
            startAdornment: (
              <InputAdornment position="start">
                🔍
              </InputAdornment>
            ),
          },
        }}
      />

      {/* Conteggio e griglia risultati */}
      {filteredFestivals.length === 0 ? (
        <Typography variant="body1" color="text.secondary" align="center">
          Nessun festival trovato corrispondente a "{searchName}".
        </Typography>
      ) : (
        <Grid container spacing={3}>
          {filteredFestivals.map((festival) => (
            <Grid size={{ xs: 12, sm: 6, md: 4 }} key={festival.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h6" component="div" gutterBottom>
                    {festival.nome}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    📍 {festival.citta} {festival.anno ? `• Anno ${festival.anno}` : ''}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                    Periodo: {festival.dataInizio} - {festival.dataFine}
                  </Typography>
                  {festival.descrizione && (
                    <Typography
                      variant="body2"
                      color="text.primary"
                      sx={{
                        mt: 1.5,
                        display: '-webkit-box',
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden',
                      }}
                    >
                      {festival.descrizione}
                    </Typography>
                  )}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
}