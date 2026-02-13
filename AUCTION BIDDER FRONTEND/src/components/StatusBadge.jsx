import React from 'react';
import { Chip } from '@mui/material';

const StatusBadge = ({ status }) => {
  let color = 'default';
  if (status === 'OPEN') color = 'success';
  if (status === 'CLOSED') color = 'error';
  if (status === 'WON') color = 'primary';

  return <Chip label={status} color={color} size="small" />;
};

export default StatusBadge;
