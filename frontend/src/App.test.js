import { render, screen } from '@testing-library/react';
import App from './App';

test('renders agent dashboard navigation', () => {
  render(<App />);

  const navigationButton = screen.getByText(/agent dashboard/i);

  expect(navigationButton).toBeInTheDocument();
});
