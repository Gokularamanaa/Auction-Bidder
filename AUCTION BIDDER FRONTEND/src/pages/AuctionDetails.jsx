import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Typography, Paper, Grid, TextField, Button, Box, List, ListItem, ListItemText, Divider, Alert, CircularProgress, Chip } from '@mui/material';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import StatusBadge from '../components/StatusBadge';
import { getAuctionById } from '../api/auctionService';
import { placeBid, getBidsForAuction } from '../api/bidService';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';

export default function AuctionDetails() {
  const { id } = useParams();
  const [auction, setAuction] = useState(null);
  const [bids, setBids] = useState([]);
  const [bidAmount, setBidAmount] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const clientRef = useRef(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [auctionRes, bidsRes] = await Promise.all([
          getAuctionById(id),
          getBidsForAuction(id)
        ]);
        setAuction(auctionRes);
        setBids(Array.isArray(bidsRes) ? bidsRes : []);
        setLoading(false);
      } catch (error) {
        console.error("Error loading auction details", error);
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  useEffect(() => {
    if (auction?.title) {
      document.title = `${auction.title} | AuctionBidder`;
    }
  }, [auction]);

  useEffect(() => {
    // WebSocket connection
    const token = localStorage.getItem('token');
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('Connected to WS');
        client.subscribe(`/topic/auction/${id}`, (msg) => {
          try {
            const newBid = JSON.parse(msg.body);
            setBids(prev => [newBid, ...prev]);
            setAuction(prev => ({ ...prev, currentHighBid: newBid.amount }));
          } catch (e) {
            console.error("Error parsing WS message", e);
          }
        });
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [id]);

  const handleBid = async (e) => {
    e.preventDefault();
    setMessage('');
    try {
      await placeBid(id, bidAmount);
      setMessage('Bid placed successfully!');
      setBidAmount('');
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      console.error("Bid failed", error);
      setMessage('Failed to place bid: ' + (error.response?.data?.message || 'Unknown error'));
    }
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}><CircularProgress /></Box>;
  if (!auction) return <Typography sx={{ mt: 10, textAlign: 'center' }}>Auction not found.</Typography>;

  return (
    <Container maxWidth="lg" sx={{ mt: 6, mb: 6 }}>
      <Paper elevation={3} sx={{ overflow: 'hidden', borderRadius: 4, bgcolor: 'background.paper' }}>
        <Box sx={{ p: 4, borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
            <Typography variant="h3" fontWeight="bold" sx={{ color: 'primary.main' }}>{auction.title || `Auction #${auction.auctionId}`}</Typography>
            <StatusBadge status={auction.status} />
          </Box>
          <Typography variant="h6" color="text.secondary" fontWeight="400">{auction.description}</Typography>
        </Box>

        <Grid container sx={{ minHeight: '400px' }}>
          <Grid item xs={12} md={8} sx={{ p: 5, borderRight: { md: '1px solid #e0e0e0' } }}>

            <Box sx={{ display: 'flex', gap: 6, mb: 6 }}>
              <Box>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 1, letterSpacing: '0.05em' }}>
                  <MonetizationOnIcon sx={{ fontSize: 20 }} /> CURRENT BID
                </Typography>
                <Typography variant="h3" fontWeight="bold" color="primary.main" sx={{ letterSpacing: '-0.02em' }}>
                  ₹{auction.currentHighBid || auction.startingPrice || 0}
                </Typography>
              </Box>
              <Box>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 1, letterSpacing: '0.05em' }}>
                  <AccessTimeIcon sx={{ fontSize: 20 }} /> ENDS IN
                </Typography>
                <Typography variant="h5" fontWeight="500" sx={{ mt: 1 }}>
                  {auction.endTime ? new Date(auction.endTime).toLocaleDateString() : 'N/A'}
                </Typography>
              </Box>
            </Box>

            <Box component="form" onSubmit={handleBid} sx={{ p: 4, bgcolor: '#f8f9fa', borderRadius: 3, border: '1px solid #e0e0e0' }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="600" sx={{ mb: 2 }}>Place Your Bid</Typography>
              <Box sx={{ display: 'flex', gap: 2, alignItems: 'stretch' }}>
                <TextField
                  label="Enter Amount"
                  type="number"
                  fullWidth
                  variant="outlined"
                  value={bidAmount}
                  onChange={(e) => setBidAmount(e.target.value)}
                  InputProps={{
                    inputProps: { step: 100 },
                    startAdornment: <Typography sx={{ mr: 1, color: 'text.secondary', fontWeight: 'bold' }}>₹</Typography>,
                    sx: { bgcolor: 'background.paper' }
                  }}
                  disabled={auction.status !== 'LIVE'}
                />
                <Button
                  type="submit"
                  variant="contained"
                  size="large"
                  disabled={auction.status !== 'LIVE' || !bidAmount}
                  sx={{ px: 5, fontWeight: 'bold', boxShadow: 'none' }}
                >
                  BID
                </Button>
              </Box>
              {message && (
                <Alert severity={message.includes('successfully') ? 'success' : 'error'} sx={{ mt: 2 }}>
                  {message}
                </Alert>
              )}
            </Box>
          </Grid>

          <Grid item xs={12} md={4} sx={{ bgcolor: '#fafafa', borderLeft: '1px solid #f0f0f0' }}>
            <Box sx={{ p: 3, borderBottom: '1px solid #e0e0e0', bgcolor: '#f5f5f5' }}>
              <Typography variant="subtitle1" fontWeight="700" color="text.secondary" sx={{ letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.85rem' }}>
                Live Bid History
              </Typography>
            </Box>
            <List sx={{ maxHeight: 500, overflow: 'auto', p: 0 }}>
              {bids.length === 0 ?
                <Box sx={{ p: 4, textAlign: 'center' }}>
                  <Typography variant="body2" color="text.secondary">No bids yet. Be the first!</Typography>
                </Box>
                :
                bids.map((bid, index) => (
                  <React.Fragment key={bid.id || index}>
                    <ListItem sx={{
                      py: 2,
                      px: 3,
                      bgcolor: index === 0 ? '#fff' : 'transparent',
                      borderLeft: index === 0 ? '4px solid #4caf50' : '4px solid transparent'
                    }}>
                      <ListItemText
                        primary={
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 0.5 }}>
                            <Typography variant="h6" fontWeight="bold" color={index === 0 ? 'success.main' : 'text.primary'}>
                              ₹{bid.amount}
                            </Typography>
                            {index === 0 && <Chip label="Highest" size="small" color="success" sx={{ height: 20, fontSize: '0.7rem', fontWeight: 'bold' }} />}
                          </Box>
                        }
                        secondary={
                          <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                            <span style={{ fontWeight: 600, color: '#555' }}>{bid.bidder && bid.bidder.name ? bid.bidder.name : `User #${bid.bidder?.id || 'Unknown'}`}</span>
                            {' • '}
                            {bid.bidTime ? new Date(bid.bidTime).toLocaleTimeString() : 'Just now'}
                          </Typography>
                        }
                      />
                    </ListItem>
                    <Divider component="li" />
                  </React.Fragment>
                ))}
            </List>
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
}
