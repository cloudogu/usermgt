import { withRouter } from 'storybook-addon-react-router-v6'
import { Navbar } from '../components/Navbar'
import { UserGroupIcon, UsersIcon } from '@heroicons/react/24/outline'

export default {
  title: 'Navbar',
  component: Navbar,
  decorators: [withRouter]
}

// @ts-ignore
const Template = (args) => <Navbar {...args} />
export const Primary = Template.bind({})
// @ts-ignore
Primary.args = {
  sites: [{ name: 'Users', path: '/users', icon: UsersIcon }, { name: 'Groups', path: '/groups', icon: UserGroupIcon }],
  currentPath: '/users',
  toolName: 'User Management'
}
