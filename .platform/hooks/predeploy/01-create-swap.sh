#!/bin/bash

SWAPFILE="/var/swapfile"
SIZE="2G"

# Check if swap already exists
if ! swapon --show | grep -q "$SWAPFILE"; then
  echo "Creating swap at $SWAPFILE with size $SIZE"
  sudo dd if=/dev/zero of=$SWAPFILE bs=1M count=2048
  sudo chmod 600 $SWAPFILE
  sudo mkswap $SWAPFILE
  sudo swapon $SWAPFILE

  # Make swap persist across reboots
  echo "$SWAPFILE none swap sw 0 0" | sudo tee -a /etc/fstab
else
  echo "Swap already exists, skipping."
fi
