-- Fix qr_code column to support TEXT type for storing Base64 QR code data
-- Run this script in MySQL if you encounter "Data too long for column 'qr_code'" error

USE iot_rental;

-- Modify the qr_code column to TEXT type
ALTER TABLE borrowing_requests 
MODIFY COLUMN qr_code TEXT;

